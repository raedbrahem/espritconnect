import argparse
import queue
import sys
import sounddevice as sd
from vosk import Model, KaldiRecognizer
import subprocess
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
import threading

# Initialize FastAPI
app = FastAPI()

# Create a queue for audio data
q = queue.Queue()

# Helper function to parse arguments (replacing argparse in the original script)
def int_or_str(text):
    """Helper function for argument parsing."""
    try:
        return int(text)
    except ValueError:
        return text

# Callback function for processing audio chunks
def callback(indata, frames, time, status):
    """This is called (from a separate thread) for each audio block."""
    if status:
        print(status, file=sys.stderr)
    q.put(bytes(indata))

# Audio transcription process
def start_transcription(model_lang="en-us"):
    """Function to handle audio input and transcription."""
    try:
        # Model selection
        if model_lang is None:
            model = Model(lang="en-us")
        else:
            model = Model(lang=model_lang)

        # Start the audio stream
        with sd.RawInputStream(samplerate=16000, blocksize=8000, device=None,
                               dtype="int16", channels=1, callback=callback):
            print("Recording started... Press Ctrl+C to stop.")
            rec = KaldiRecognizer(model, 16000)

            # Process audio and transcribe
            while True:
                data = q.get()
                if rec.AcceptWaveform(data):
                    print(rec.Result())
                else:
                    print(rec.PartialResult())
    except KeyboardInterrupt:
        print("\nTranscription stopped")
    except Exception as e:
        print(f"Error: {e}")

# WebSocket endpoint to start transcription
@app.websocket("/ws/transcribe")
async def websocket_transcribe(websocket: WebSocket):
    """WebSocket endpoint to stream live transcription results."""
    await websocket.accept()

    # Start transcription in a background thread
    thread = threading.Thread(target=start_transcription, args=("en-us",))
    thread.daemon = True  # This ensures the thread will exit when the main process exits
    thread.start()

    try:
        # Send real-time transcription results to the WebSocket client
        while True:
            # Example: Here we just send output as a message, can be customized to send transcribed text
            output = q.get()  # In real case, you might want to stream this data back to client
            await websocket.send_text(output.decode('utf-8'))

    except WebSocketDisconnect:
        print("WebSocket connection closed")

# Run the app
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
