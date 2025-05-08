package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.FCMService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/fcm")
@CrossOrigin(origins = "http://localhost:4200")
public class FCMController {
    private static final Logger logger = Logger.getLogger(FCMController.class.getName());

    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostandFoundRepository itemRepository;

    @Autowired
    private ProofRepository proofRepository;

    /**
     * Test the FCM connection
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection(@RequestParam(required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is required");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Testing FCM connection with token: " + token);

            // Test the FCM connection
            String result = fcmService.testFCMConnection(token);

            response.put("success", result.startsWith("✅"));
            response.put("message", result);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error testing FCM connection", e);

            response.put("success", false);
            response.put("message", "Error testing FCM connection: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Send a notification to a token
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Sending notification to token: " + token);

            // Send notification
            String result = fcmService.sendNotification(token, title, body);

            response.put("success", result.startsWith("✅"));
            response.put("message", result);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending notification", e);

            response.put("success", false);
            response.put("message", "Error sending notification: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Send a notification to a user
     */
    @PostMapping("/send-to-user")
    public ResponseEntity<Map<String, Object>> sendNotificationToUser(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String body
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find user
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Sending notification to user: " + user.getEmail());

            // Send notification
            String result = fcmService.sendNotification(user.getFcmToken(), title, body);

            response.put("success", result.startsWith("✅"));
            response.put("message", result);
            response.put("user", user.getEmail());
            response.put("token", user.getFcmToken());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending notification to user", e);

            response.put("success", false);
            response.put("message", "Error sending notification to user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Send a match notification
     */
    @PostMapping("/send-match")
    public ResponseEntity<Map<String, Object>> sendMatchNotification(
            @RequestParam Long lostItemId,
            @RequestParam Long proofId,
            @RequestParam(defaultValue = "0.8") double confidence
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find lost item
            Item lostItem = itemRepository.findById(lostItemId).orElse(null);
            if (lostItem == null) {
                response.put("success", false);
                response.put("message", "Lost item not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Find proof
            Proof proof = proofRepository.findById(proofId).orElse(null);
            if (proof == null) {
                response.put("success", false);
                response.put("message", "Proof not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Find user
            User user = lostItem.getProprietaire();
            if (user == null) {
                response.put("success", false);
                response.put("message", "Lost item has no owner");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Sending match notification to user: " + user.getEmail());

            // Send notification
            String result = fcmService.sendMatchNotification(user, lostItem, proof, confidence);

            response.put("success", result.startsWith("✅"));
            response.put("message", result);
            response.put("user", user.getEmail());
            response.put("lostItem", lostItem.getItem_name());
            response.put("proof", proof.getId_proof());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending match notification", e);

            response.put("success", false);
            response.put("message", "Error sending match notification: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Send a confirmation notification
     */
    @PostMapping("/send-confirmation")
    public ResponseEntity<Map<String, Object>> sendConfirmationNotification(
            @RequestParam Long lostItemId,
            @RequestParam Long proofId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find lost item
            Item lostItem = itemRepository.findById(lostItemId).orElse(null);
            if (lostItem == null) {
                response.put("success", false);
                response.put("message", "Lost item not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Find proof
            Proof proof = proofRepository.findById(proofId).orElse(null);
            if (proof == null) {
                response.put("success", false);
                response.put("message", "Proof not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Find user
            User user = lostItem.getProprietaire();
            if (user == null) {
                response.put("success", false);
                response.put("message", "Lost item has no owner");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Sending confirmation notification to user: " + user.getEmail());

            // Send notification
            String result = fcmService.sendConfirmationNotification(user, lostItem, proof);

            response.put("success", result.startsWith("✅"));
            response.put("message", result);
            response.put("user", user.getEmail());
            response.put("lostItem", lostItem.getItem_name());
            response.put("proof", proof.getId_proof());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending confirmation notification", e);

            response.put("success", false);
            response.put("message", "Error sending confirmation notification: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Update a user's FCM token
     */
    @PostMapping("/update-token")
    public ResponseEntity<Map<String, Object>> updateToken(
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find user
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Updating FCM token for user: " + user.getEmail());

            // Update token
            user.setFcmToken(token);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "FCM token updated successfully");
            response.put("user", user.getEmail());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating FCM token", e);

            response.put("success", false);
            response.put("message", "Error updating FCM token: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
