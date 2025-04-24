package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FCMService {
    private static final Logger logger = Logger.getLogger(FCMService.class.getName());

    @Autowired(required = false)
    private FirebaseApp firebaseApp;

    private FirebaseMessaging firebaseMessaging;
    private boolean isInitialized = false;

    /**
     * Initialize Firebase Messaging after bean construction
     */
    @PostConstruct
    public void init() {
        initializeFirebaseMessaging();
    }

    /**
     * Initialize Firebase Messaging
     */
    private void initializeFirebaseMessaging() {
        try {
            if (firebaseApp != null) {
                this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
                this.isInitialized = true;
                logger.info("FCMService initialized with injected FirebaseApp: " + firebaseApp.getName());
            } else {
                // Try to get the default instance
                try {
                    if (!FirebaseApp.getApps().isEmpty()) {
                        FirebaseApp defaultApp = FirebaseApp.getInstance();
                        this.firebaseMessaging = FirebaseMessaging.getInstance(defaultApp);
                        this.isInitialized = true;
                        logger.info("FCMService initialized with default FirebaseApp: " + defaultApp.getName());
                    } else {
                        logger.warning("No FirebaseApp instances available");
                        this.isInitialized = false;
                    }
                } catch (Exception e) {
                    logger.severe("Error getting default FirebaseApp: " + e.getMessage());
                    this.isInitialized = false;
                }
            }
        } catch (Exception e) {
            logger.severe("Error initializing FCMService: " + e.getMessage());
            e.printStackTrace();
            this.isInitialized = false;
        }
    }

    /**
     * Send a notification to a specific device token
     */
    public String sendNotification(String token, String title, String body) {
        return sendNotification(token, title, body, null);
    }

    /**
     * Send a notification to a specific device token with data
     */
    public String sendNotification(String token, String title, String body, Map<String, String> data) {
        try {
            // Validate parameters
            if (token == null || token.isEmpty()) {
                logger.warning("Cannot send notification: FCM token is null or empty");
                return "⚠️ FCM token is null or empty. Notification not sent.";
            }

            // Check if Firebase Messaging is initialized
            if (!isInitialized || firebaseMessaging == null) {
                logger.warning("Firebase Messaging is not initialized, trying to initialize...");
                initializeFirebaseMessaging();

                if (!isInitialized || firebaseMessaging == null) {
                    logger.severe("Failed to initialize Firebase Messaging");
                    return "❌ Firebase Messaging is not initialized. Notification not sent.";
                }
            }

            logger.info("Sending FCM notification to token: " + token);
            logger.info("Title: " + title);
            logger.info("Body: " + body);

            // Create notification
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Create message builder
            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(notification);

            // Add data if provided
            if (data != null && !data.isEmpty()) {
                logger.info("Adding data to notification: " + data);
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    messageBuilder.putData(entry.getKey(), entry.getValue());
                }
            } else {
                // Add default data
                messageBuilder.putData("click_action", "FLUTTER_NOTIFICATION_CLICK");
            }

            // Build and send message
            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);

            logger.info("FCM notification sent successfully: " + response);
            return "✅ Notification sent: " + response;
        } catch (FirebaseMessagingException e) {
            logger.log(Level.SEVERE, "Error sending FCM notification", e);
            return "❌ Error sending notification: " + e.getMessage();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in FCM notification", e);
            return "❌ Unexpected error: " + e.getMessage();
        }
    }

    /**
     * Send a match notification to a user
     */
    public String sendMatchNotification(User user, Item lostItem, Proof proof, double matchConfidence) {
        try {
            // Validate parameters
            if (user == null) {
                logger.warning("Cannot send match notification: User is null");
                return "⚠️ User is null. Notification not sent.";
            }

            if (lostItem == null) {
                logger.warning("Cannot send match notification: Lost item is null");
                return "⚠️ Lost item is null. Notification not sent.";
            }

            if (proof == null) {
                logger.warning("Cannot send match notification: Proof is null");
                return "⚠️ Proof is null. Notification not sent.";
            }

            String token = user.getFcmToken();
            if (token == null || token.isEmpty()) {
                logger.warning("Cannot send match notification: User has no FCM token");
                return "⚠️ User has no FCM token. Notification not sent.";
            }

            // Create notification title and body
            String title = "Match Found! 🔍";
            String body = "We found a potential match for your lost " + lostItem.getItem_name() + " with "
                    + (int)(matchConfidence * 100) + "% confidence.";

            // Create data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "match_notification");
            data.put("lostItemId", String.valueOf(lostItem.getId_item()));
            data.put("proofId", String.valueOf(proof.getId_proof()));
            data.put("confidence", String.valueOf(matchConfidence));
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");

            // Send notification
            return sendNotification(token, title, body, data);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending match notification", e);
            return "❌ Error sending match notification: " + e.getMessage();
        }
    }

    /**
     * Send a confirmation notification to a user
     */
    public String sendConfirmationNotification(User user, Item lostItem, Proof proof) {
        try {
            // Validate parameters
            if (user == null) {
                logger.warning("Cannot send confirmation notification: User is null");
                return "⚠️ User is null. Notification not sent.";
            }

            if (lostItem == null) {
                logger.warning("Cannot send confirmation notification: Lost item is null");
                return "⚠️ Lost item is null. Notification not sent.";
            }

            if (proof == null) {
                logger.warning("Cannot send confirmation notification: Proof is null");
                return "⚠️ Proof is null. Notification not sent.";
            }

            String token = user.getFcmToken();
            if (token == null || token.isEmpty()) {
                logger.warning("Cannot send confirmation notification: User has no FCM token");
                return "⚠️ User has no FCM token. Notification not sent.";
            }

            // Create notification title and body
            String title = "Item Found! 🎉";
            String body = "Great news! Your lost " + lostItem.getItem_name() + " has been found and confirmed.";

            // Create data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "confirmation_notification");
            data.put("lostItemId", String.valueOf(lostItem.getId_item()));
            data.put("proofId", String.valueOf(proof.getId_proof()));
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");

            // Send notification
            return sendNotification(token, title, body, data);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending confirmation notification", e);
            return "❌ Error sending confirmation notification: " + e.getMessage();
        }
    }


    /**
     * Send a simple notification to a user
     */
    public String sendSimpleNotification(User user, String title, String body) {
        try {
            if (user == null) {
                logger.warning("Cannot send simple notification: User is null");
                return "⚠️ User is null. Notification not sent.";
            }

            String token = user.getFcmToken();
            if (token == null || token.isEmpty()) {
                logger.warning("Cannot send simple notification: User has no FCM token");
                return "⚠️ User has no FCM token. Notification not sent.";
            }

            // Create data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "simple_notification");
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");

            // Send notification
            return sendNotification(token, title, body, data);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending simple notification", e);
            return "❌ Error sending simple notification: " + e.getMessage();
        }
    }

    /**
     * Test the FCM connection
     */
    public String testFCMConnection(String token) {
        try {
            if (token == null || token.isEmpty()) {
                logger.warning("Cannot test FCM connection: Token is null or empty");
                return "⚠️ Token is null or empty. Test not performed.";
            }

            logger.info("Testing FCM connection with token: " + token);

            // Send a test notification
            return sendNotification(token, "Test Notification", "This is a test notification from the FCM service");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error testing FCM connection", e);
            return "❌ Error testing FCM connection: " + e.getMessage();
        }
    }

    /**
     * Check if the FCM service is initialized
     */
    public boolean isInitialized() {
        return isInitialized && firebaseMessaging != null;
    }
}
