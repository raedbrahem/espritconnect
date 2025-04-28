from flask import Flask, request, jsonify
import queue
import sounddevice as sd
from vosk import Model, KaldiRecognizer
import argparse
import sys
import os
import tempfile
from flask_cors import CORS  # Add this import

# Initialize the Flask app
app = Flask(__name__)

# Enable CORS for specific routes
CORS(app, resources={
    r"/start_recording": {"origins": "http://localhost:4200"},
    r"/get_transcription": {"origins": "http://localhost:4200"},
    r"/stop_recording": {"origins": "http://localhost:4200"},
    r"/start_recording": {"origins": "http://localhost:8089"},
    r"/get_transcription": {"origins": "http://localhost:8089"},
    r"/stop_recording": {"origins": "http://localhost:8089"}
})

# Global variables for the audio stream
q = queue.Queue()
stream = None
rec = None

def callback(indata, frames, time, status):
    """Audio callback function"""
    if status:
        print(status, file=sys.stderr)
    q.put(bytes(indata))

@app.route('/start_recording', methods=['POST'])
def start_recording():
    global stream, rec, q
    
    # Get parameters from request
    data = request.json
    language = data.get('language', 'en-us')
    device_id = data.get('device_id', None)
    sample_rate = data.get('sample_rate', None)
    
    try:
        # Load model
        model = Model(lang=language)
        
        # Set sample rate if not provided
        if sample_rate is None and device_id is not None:
            device_info = sd.query_devices(device_id, "input")
            sample_rate = int(device_info["default_samplerate"])
        elif sample_rate is None:
            sample_rate = 16000  # default sample rate
            
        # Initialize recognizer
        rec = KaldiRecognizer(model, sample_rate)
        
        # Start audio stream
        stream = sd.RawInputStream(
            samplerate=sample_rate,
            blocksize=8000,
            device=device_id,
            dtype="int16",
            channels=1,
            callback=callback
        )
        stream.start()
        
        return jsonify({"status": "recording started", "sample_rate": sample_rate})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/get_transcription', methods=['GET'])
def get_transcription():
    global rec, q
    
    if rec is None:
        return jsonify({"error": "Recording not started"}), 400
        
    try:
        if not q.empty():
            data = q.get()
            if rec.AcceptWaveform(data):
                result = rec.Result()
            else:
                result = rec.PartialResult()
            return jsonify({"transcription": result})
        else:
            return jsonify({"transcription": ""})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/stop_recording', methods=['POST'])
def stop_recording():
    global stream
    if stream is not None:
        stream.stop()
        stream.close()
        stream = None
    return jsonify({"status": "recording stopped"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
