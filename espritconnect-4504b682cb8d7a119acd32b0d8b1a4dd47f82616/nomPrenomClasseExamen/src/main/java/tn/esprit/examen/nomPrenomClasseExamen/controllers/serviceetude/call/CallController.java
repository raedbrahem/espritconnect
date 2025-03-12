package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude.call;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calls")
public class CallController {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    // Endpoint to create a call
    @PostMapping("/create")
    public String createCall() {
        // Create a reference to "calls" in Firebase Realtime Database
        DatabaseReference callsRef = firebaseDatabase.getReference("calls");

        // Generate a unique call ID
        String callId = callsRef.push().getKey();

        // Store call-related data (e.g., offer) in Firebase under the generated callId
        DatabaseReference callRef = callsRef.child(callId);
        callRef.setValueAsync("call_data");  // Replace with your actual call data (e.g., offer)

        return callId;  // Return the created callId to the frontend
    }

    // Endpoint to answer the call
    @PostMapping("/answer/{callId}")
    public String answerCall(@PathVariable String callId, @RequestBody String answer) {
        // Create a reference to the specific call by its callId
        DatabaseReference callRef = firebaseDatabase.getReference("calls").child(callId);
        // Store the answer (e.g., SDP answer) in the Firebase Realtime Database
        callRef.child("answer").setValueAsync(answer);
        return "Call answered";
    }

    // Endpoint to send ICE candidates to Firebase
    @PostMapping("/candidate/{callId}")
    public String addCandidate(@PathVariable String callId, @RequestBody String candidate) {
        // Create a reference to the specific call's ICE candidates
        DatabaseReference callRef = firebaseDatabase.getReference("calls").child(callId);

        // Push the ICE candidate to the Firebase Realtime Database
        callRef.child("candidates").push().setValueAsync(candidate);

        return "Candidate added";
    }
}
