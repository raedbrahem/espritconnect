package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ServiceFeedbackImpl implements IServiceFeedback {
    private final FeedbackRepository feedbackRepo;

    @Override
    public Feedback addFeedback(Feedback feedback) {
        feedback.setSubmittedAt(LocalDateTime.now());
        return feedbackRepo.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepo.findAll();
    }

    @Override
    public List<Feedback> getFeedbackByItem(Long itemId) {
        return feedbackRepo.findByItemId(itemId);
    }

    @Override
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepo.findByUserId(userId);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepo.deleteById(id);
    }
}
