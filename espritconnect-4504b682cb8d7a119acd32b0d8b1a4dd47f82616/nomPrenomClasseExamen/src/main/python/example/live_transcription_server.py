from vosk import Model, KaldiRecognizer
import pyaudio
import json
from flask import Flask
from flask_socketio import SocketIO, emit

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*")

model = Model("model")  # Path to your Vosk model
recognizer = KaldiRecognizer(model, 16000)
mic = pyaudio.PyAudio()

def callback(in_data, frame_count, time_info, status):
    if recognizer.AcceptWaveform(in_data):
        result = json.loads(recognizer.Result())
        socketio.emit('transcription', result['text'])
    return (in_data, pyaudio.paContinue)

@socketio.on('start_recording')
def handle_start():
    stream = mic.open(format=pyaudio.paInt16,
                     channels=1,
                     rate=16000,
                     input=True,
                     frames_per_buffer=8192,
                     stream_callback=callback)
    stream.start_stream()
    emit('status', {'message': 'Recording started'})

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=5000)