const FIREBASE_API_URL = "http://localhost:8089/api/calls"; // Update this with your backend URL

// ICE Server configuration
const servers = {
    iceServers: [
        { urls: ["stun:stun1.l.google.com:19302", "stun:stun2.l.google.com:19302"] },
    ],
    iceCandidatePoolSize: 10,
};

// Global State
const pc = new RTCPeerConnection(servers);
let localStream = null;
let remoteStream = null;

// HTML elements
const webcamButton = document.getElementById("webcamButton");
const webcamVideo = document.getElementById("webcamVideo");
const callButton = document.getElementById("callButton");
const callInput = document.getElementById("callInput");
const answerButton = document.getElementById("answerButton");
const remoteVideo = document.getElementById("remoteVideo");
const hangupButton = document.getElementById("hangupButton");

// 1. Setup media sources
webcamButton.onclick = async () => {
    try {
        localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        remoteStream = new MediaStream();

        localStream.getTracks().forEach(track => pc.addTrack(track, localStream));

        pc.ontrack = (event) => {
            event.streams[0].getTracks().forEach(track => remoteStream.addTrack(track));
        };

        webcamVideo.srcObject = localStream;
        remoteVideo.srcObject = remoteStream;

        callButton.disabled = false;
        answerButton.disabled = false;
        webcamButton.disabled = true;
    } catch (err) {
        console.error("Error getting user media:", err);
    }
};

// 2. Create a new call
callButton.onclick = async () => {
    try {
        // Request the backend to create a call
        const response = await fetch(`${FIREBASE_API_URL}/create`, {
            method: "POST",
        });
        const callId = await response.text(); // Backend returns the callId

        callInput.value = callId; // Set callId in input field

        pc.onicecandidate = (event) => {
            if (event.candidate) {
                fetch(`${FIREBASE_API_URL}/candidate/${callId}`, {
                    method: "POST",
                    body: JSON.stringify(event.candidate),
                    headers: { "Content-Type": "application/json" },
                }).catch(error => console.error("Error sending ICE candidate:", error));
            }
        };

        const offerDescription = await pc.createOffer();
        await pc.setLocalDescription(offerDescription);

        // Send offer to backend
        await fetch(`${FIREBASE_API_URL}/answer/${callId}`, {
            method: "POST",
            body: JSON.stringify(offerDescription.sdp),
            headers: { "Content-Type": "application/json" },
        });

        pollForAnswer(callId);
        hangupButton.disabled = false;
    } catch (err) {
        console.error("Error creating call:", err);
    }
};

// 3. Answer a call
answerButton.onclick = async () => {
    try {
        const callId = callInput.value;

        // Fetch offer from backend (await the fetch call)
        const response = await fetch(`${FIREBASE_API_URL}/answer/${callId}`);
        const offerSdp = await response.text(); // Make sure to get the SDP offer response

        await pc.setRemoteDescription(new RTCSessionDescription({ type: "offer", sdp: offerSdp }));

        const answerDescription = await pc.createAnswer();
        await pc.setLocalDescription(answerDescription);

        // Send answer back to backend
        await fetch(`${FIREBASE_API_URL}/answer/${callId}`, {
            method: "POST",
            body: JSON.stringify(answerDescription.sdp),
            headers: { "Content-Type": "application/json" },
        });

        pollForCandidates(callId);
    } catch (err) {
        console.error("Error answering call:", err);
    }
};


// 4. Poll for remote answer
async function pollForAnswer(callId) {
    const interval = setInterval(async () => {
        const response = await fetch(`${FIREBASE_API_URL}/answer/${callId}`);
        const answerSdp = await response.text();
        if (answerSdp) {
            clearInterval(interval);
            await pc.setRemoteDescription(new RTCSessionDescription({ type: "answer", sdp: answerSdp }));
        }
    }, 2000);
}

// 5. Poll for ICE candidates
async function pollForCandidates(callId) {
    const interval = setInterval(async () => {
        const response = await fetch(`${FIREBASE_API_URL}/${callId}/candidates`);
        const candidates = await response.json();
        if (candidates.length > 0) {
            clearInterval(interval);
            candidates.forEach(candidateData => {
                const candidate = new RTCIceCandidate(candidateData);
                pc.addIceCandidate(candidate);
            });
        }
    }, 2000);
}
