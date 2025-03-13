package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ITutoringRepository;

import java.util.List;

@Service
public class ServiceTutoringEventImpl implements IServiceTutoringEvent {

    @Autowired
    private ITutoringRepository tutoringEventRepository;

    @Override
    public TutoringEvent addTutoringEvent(TutoringEvent tutoringEvent) {
        return tutoringEventRepository.save(tutoringEvent);
    }

    @Override
    public void deleteTutoringEvent(TutoringEvent tutoringEvent) {
        tutoringEventRepository.delete(tutoringEvent);
    }

    @Override
    public TutoringEvent updateTutoringEvent(TutoringEvent tutoringEvent) {
        return tutoringEventRepository.save(tutoringEvent);
    }

    @Override
    public List<TutoringEvent> getAllTutoringEvents() {
        return tutoringEventRepository.findAll();
    }
}