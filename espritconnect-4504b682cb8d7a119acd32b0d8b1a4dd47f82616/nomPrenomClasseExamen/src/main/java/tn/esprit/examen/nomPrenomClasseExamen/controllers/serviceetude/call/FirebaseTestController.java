    package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude.call;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Call;

    @RestController
    @RequestMapping("/api/test")
    public class FirebaseTestController {

        private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        @GetMapping("/write")
        public String writeTestData() {
            // Write some test data to Firebase
            DatabaseReference ref = firebaseDatabase.getReference("test");
            ref.setValueAsync("Hello, Firebase!");

            return "Data written to Firebase";
        }

        @GetMapping("/read")
        public String readTestData() {
            // Read the test data from Firebase
            DatabaseReference ref = firebaseDatabase.getReference("test");

            // Using ValueEventListener to get the data
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Handle the data snapshot
                    Object value = dataSnapshot.getValue();
                    System.out.println("Data from Firebase: " + value);
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                    // Handle error
                    System.out.println("Error getting data: " + databaseError.getMessage());
                }
            });

            return "Check the console for the test data";
        }

        @GetMapping("/listen")
        public String listenDataChanges() {
            // Listen to changes on "test" in real-time
            DatabaseReference ref = firebaseDatabase.getReference("test");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Handle updated data
                    Object value = dataSnapshot.getValue();
                    System.out.println("Updated Data from Firebase: " + value);
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                    // Handle error
                    System.out.println("Error listening to data: " + databaseError.getMessage());
                }
            });

            return "Listening to data changes in Firebase. Check the console for updates.";
        }


        @PostMapping("/create")
        public String createCall() {
            // Generate a unique call ID
            String callId = firebaseDatabase.getReference("calls").push().getKey();
            // Create the Call object
            Call call = new Call(callId, "offer_data"); // You should replace "offer_data" with actual offer info

            // Store the call in Firebase
            DatabaseReference callRef = firebaseDatabase.getReference("calls").child(callId);
            callRef.setValueAsync(call);

            return "Call created with ID: " + callId;
        }

    }
