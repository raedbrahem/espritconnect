package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ITutoringRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ServiceEtudeRepository;

import java.util.List;

@Service
public class ServiceTutoringEventImpl implements IServiceTutoringEvent {

    @Autowired
    private ITutoringRepository tutoringEventRepository;

    @Autowired
    private IServiceEtude serviceEtudeService; // You'll need to create this if it doesn't exist
    @Autowired
    private ServiceEtudeRepository serviceEtudeRepository;

    @Override
    public TutoringEvent addTutoringEvent(TutoringEvent tutoringEvent) {
        // Load the complete ServiceEtude entity
        Service_Etude serviceEtude = serviceEtudeRepository.findById(tutoringEvent.getServiceEtude().getId())
                .orElseThrow(() -> new RuntimeException("ServiceEtude not found"));

        // Set the tutor from ServiceEtude
        tutoringEvent.setTutor(serviceEtude.getTutor());

        // Verify student exists (optional)
        if (tutoringEvent.getStudent().getId() == null) {
            throw new RuntimeException("Student ID must be provided");
        }

        return tutoringEventRepository.save(tutoringEvent);
    }

    @Override
    public void deleteTutoringEvent(TutoringEvent tutoringEvent) {
        tutoringEventRepository.delete(tutoringEvent);
    }

    @Override
    public TutoringEvent updateTutoringEvent(TutoringEvent tutoringEvent) {
        // Load the existing event
        TutoringEvent existingEvent = tutoringEventRepository.findById(tutoringEvent.getId())
                .orElseThrow(() -> new RuntimeException("TutoringEvent not found"));

        // Load the complete ServiceEtude entity (whether it's being updated or not)
        Service_Etude serviceEtude = serviceEtudeRepository.findById(
                        tutoringEvent.getServiceEtude() != null ?
                                tutoringEvent.getServiceEtude().getId() :
                                existingEvent.getServiceEtude().getId())
                .orElseThrow(() -> new RuntimeException("ServiceEtude not found"));

        // Update basic fields
        existingEvent.setTitle(tutoringEvent.getTitle());
        existingEvent.setStartTime(tutoringEvent.getStartTime());
        existingEvent.setEndTime(tutoringEvent.getEndTime());
        existingEvent.setStatus(tutoringEvent.getStatus());
        existingEvent.setPrice(tutoringEvent.getPrice());

        // Update student if changed
        if (tutoringEvent.getStudent() != null && tutoringEvent.getStudent().getId() != null) {
            existingEvent.setStudent(tutoringEvent.getStudent());
        }

        // Always set tutor from serviceEtude (whether it's being updated or not)
        existingEvent.setTutor(serviceEtude.getTutor());

        // Update serviceEtude if changed
        if (tutoringEvent.getServiceEtude() != null) {
            existingEvent.setServiceEtude(serviceEtude);
        }

        return tutoringEventRepository.save(existingEvent);
    }

    @Override
    public List<TutoringEvent> getAllTutoringEvents() {
        return tutoringEventRepository.findAll();
    }

    @Override
    public List<TutoringEvent> getTutoringEventsByTutor(Long tutorId) {
        return tutoringEventRepository.findByTutorId(tutorId);
    }

    @Override
    public List<TutoringEvent> getTutoringEventsByStudent(Long studentId) {
        return tutoringEventRepository.findByStudentId(studentId);
    }

    @Override
    public TutoringEvent retrieveTutoringEvent(Long id) {
        return tutoringEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TutoringEvent not found"));
    }
}