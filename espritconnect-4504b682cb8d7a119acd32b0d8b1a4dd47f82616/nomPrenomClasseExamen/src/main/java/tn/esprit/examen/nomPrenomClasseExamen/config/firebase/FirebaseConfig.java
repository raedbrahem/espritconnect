package tn.esprit.examen.nomPrenomClasseExamen.config.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void firebaseInit() throws IOException {
        // Load the service account key file from your local filesystem or resources
        FileInputStream serviceAccount =
                new FileInputStream(getClass().getClassLoader().getResource("firebase/key.json").getFile());


        // Initialize Firebase options
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://webrtc-6a829-default-rtdb.firebaseio.com")  // Replace with your Firebase Realtime Database URL
                .build();

        // Initialize Firebase
        FirebaseApp.initializeApp(options);
    }
}
