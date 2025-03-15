package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;

import java.util.List;

public interface IServiceTutoringEvent {
    TutoringEvent addTutoringEvent(TutoringEvent tutoringEvent); // Add a new tutoring event
    void deleteTutoringEvent(TutoringEvent tutoringEvent); // Delete a tutoring event
    TutoringEvent updateTutoringEvent(TutoringEvent tutoringEvent); // Update an existing tutoring event
    List<TutoringEvent> getAllTutoringEvents(); // Retrieve all tutoring events
}