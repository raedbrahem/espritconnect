package tn.esprit.examen.nomPrenomClasseExamen.config.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void firebaseInit() throws IOException {
        // Charger le fichier depuis le dossier resources
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase/key.json");

        if (serviceAccount == null) {
            throw new IOException("Le fichier key.json est introuvable dans resources/firebase !");
        }

        // Initialiser Firebase
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://webrtc-6a829-default-rtdb.firebaseio.com") // Remplacez par votre URL
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialisé avec succès !");
        } else {
            System.out.println("⚠️ Firebase était déjà initialisé !");
        }
    }
}