package tn.esprit.examen.nomPrenomClasseExamen.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Firebase Cloud Messaging configuration
 */
@Configuration
public class    FCMConfig {
    private static final Logger logger = Logger.getLogger(FCMConfig.class.getName());

    /**
     * Create a FirebaseApp bean for FCM
     * @return FirebaseApp instance
     */
    @Bean
    @Primary
    public FirebaseApp firebaseApp() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase for FCM notifications...");
                
                // Load the service account key JSON file
                InputStream serviceAccount = new ClassPathResource("firebase/firebase-service-account.json").getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                
                // Initialize the app with the default name
                FirebaseApp app = FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully for FCM notifications!");
                return app;
            } else {
                // Return the default instance
                logger.info("Firebase already initialized, returning existing instance");
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            logger.severe("Error initializing Firebase for FCM: " + e.getMessage());
            e.printStackTrace();
            
            // Create a dummy FirebaseApp for testing
            try {
                logger.info("Attempting to create a dummy FirebaseApp for testing...");
                
                // Create a dummy options object
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(
                                new ClassPathResource("firebase/firebase-service-account.json").getInputStream()))
                        .build();
                
                // Try to initialize with a unique name to avoid conflicts
                String appName = "fcm-app-" + System.currentTimeMillis();
                FirebaseApp app = FirebaseApp.initializeApp(options, appName);
                logger.info("Created dummy FirebaseApp with name: " + appName);
                return app;
            } catch (Exception ex) {
                logger.severe("Failed to create dummy FirebaseApp: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            }
        }
    }
}
