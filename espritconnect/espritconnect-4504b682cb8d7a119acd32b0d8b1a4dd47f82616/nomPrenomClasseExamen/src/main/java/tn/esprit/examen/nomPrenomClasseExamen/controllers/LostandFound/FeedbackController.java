package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.FeedbackRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceFeedback;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.ServiceFeedbackImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final LostandFoundRepository itemRepository;

    private final ServiceFeedbackImpl service;


    /**
     * Submit feedback for an item's AI-predicted category
     *
     * @param itemId The ID of the item
     * @param correctCategoryStr Whether the AI-predicted category is correct
     * @param explanation The explanation or correct category name
     * @param newCategory The new category to set (when correctCategory is false)
     * @return A response indicating success or failure
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(
            @RequestParam("itemId") Long itemId,
            @RequestParam("correctCategory") String correctCategoryStr,
            @RequestParam("explanation") String explanation,
            @RequestParam(value = "newCategory", required = false) String newCategory) {

        // Convert string to boolean
        boolean correctCategory = Boolean.parseBoolean(correctCategoryStr);

        try {
            // Find the item
            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (!itemOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Item not found");
            }

            Item item = itemOpt.get();

            // Get current user (if authenticated)
            User user = null;
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                    Optional<User> userOpt = userRepository.findByEmail(auth.getName());
                    if (userOpt.isPresent()) {
                        user = userOpt.get();
                    }
                }
            } catch (Exception e) {
                // Continue without user if there's an error
            }

            // Create and save feedback
            Feedback feedback = new Feedback();
            feedback.setItem(item);
            feedback.setUser(user);
            feedback.setCorrectCategory(correctCategory);
            feedback.setExplanation(explanation);
            feedback.setSubmittedAt(LocalDateTime.now());

            service.saveFeedback(feedback);

            // The category update will be handled by the FeedbackService
            // We don't need to update it here as it's already done in saveFeedback

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Feedback submitted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit feedback: " + e.getMessage());
        }
    }

@PostMapping("/submitt")
    public ResponseEntity<?> submittFeedback(
            @RequestParam Long itemId,
            @RequestParam boolean correctCategory,
            @RequestParam(required = false) String explanation,
            Principal principal
    ) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setItem(item);
        feedback.setCorrectCategory(correctCategory);
        feedback.setExplanation(explanation);
        feedback.setSubmittedAt(LocalDateTime.now());

        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }
}
