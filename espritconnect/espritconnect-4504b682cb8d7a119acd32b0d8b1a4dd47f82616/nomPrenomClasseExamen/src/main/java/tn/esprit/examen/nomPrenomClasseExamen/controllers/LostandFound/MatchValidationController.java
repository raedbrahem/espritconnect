package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.ItemMatchNotification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ItemMatchNotificationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.FCMService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/match")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MatchValidationController {
    private static final Logger logger = Logger.getLogger(MatchValidationController.class.getName());

    private final ItemMatchNotificationRepository notificationRepo;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/pending")
    public List<ItemMatchNotification> getPendingMatches() {
        return notificationRepo.findByIsValidatedFalse();
    }


    @PostMapping("/validate/{id}")
    public ResponseEntity<Map<String, Object>> validateMatch(
            @PathVariable Long id,
            @RequestParam boolean accepted
    ) {
        try {
            logger.info("Validating match: " + id + ", accepted: " + accepted);

            // Find the match notification
            ItemMatchNotification match = notificationRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));

            // Update the match status
            match.setIsMatchAccepted(accepted);
            match.setValidated(true);
            notificationRepo.save(match);

            // Get the related entities
            Item lostItem = match.getMatchedItem();
            Proof proof = match.getProof();
            User user = match.getRecipient();

            // No longer using NotificationService

            // Send FCM notification if accepted
            String fcmResult = "FCM notification not sent";
            try {
                if (accepted && user != null) {
                    if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                        if (lostItem != null && proof != null) {
                            fcmResult = fcmService.sendConfirmationNotification(user, lostItem, proof);
                            logger.info("FCM notification result: " + fcmResult);
                        } else {
                            logger.warning("Cannot send FCM notification: lostItem or proof is null");
                        }
                    } else {
                        logger.warning("Cannot send FCM notification: user has no FCM token");
                    }
                }
            } catch (Exception e) {
                logger.severe("Error sending FCM notification: " + e.getMessage());
                fcmResult = "Error sending FCM notification: " + e.getMessage();
                // Continue execution even if FCM notification fails
            }

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", accepted ? "Match accepted and user notified" : "Match rejected");
            response.put("matchId", id);
            response.put("accepted", accepted);
            response.put("fcmResult", fcmResult);

            return ResponseEntity
                    .ok()
                    .header("Content-Type", "application/json") // Force correct content type
                    .body(response);
        } catch (Exception e) {
            logger.severe("Error validating match: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error validating match: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

}
