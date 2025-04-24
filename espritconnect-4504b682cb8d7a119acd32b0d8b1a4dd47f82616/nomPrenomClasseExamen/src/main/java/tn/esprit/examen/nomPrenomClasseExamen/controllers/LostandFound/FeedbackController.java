package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final LostandFoundRepository itemRepository;

    private final IServiceFeedback service;

    @PostMapping("/add")
    public Feedback addFeedback(@RequestBody Feedback feedback) {
        return service.addFeedback(feedback);
    }

    @GetMapping("/all")
    public List<Feedback> getAll() {
        return service.getAllFeedback();
    }

    @GetMapping("/by-item/{itemId}")
    public List<Feedback> getByItem(@PathVariable Long itemId) {
        return service.getFeedbackByItem(itemId);
    }

    @GetMapping("/by-user/{userId}")
    public List<Feedback> getByUser(@PathVariable Long userId) {
        return service.getFeedbackByUser(userId);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteFeedback(id);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(
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
