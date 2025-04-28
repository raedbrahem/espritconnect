package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;

import java.util.List;

public interface IServiceFeedback {
    Feedback addFeedback(Feedback feedback);
    List<Feedback> getAllFeedback();
    List<Feedback> getFeedbackByItem(Long itemId);
    List<Feedback> getFeedbackByUser(Long userId);
    void deleteFeedback(Long id);
}
