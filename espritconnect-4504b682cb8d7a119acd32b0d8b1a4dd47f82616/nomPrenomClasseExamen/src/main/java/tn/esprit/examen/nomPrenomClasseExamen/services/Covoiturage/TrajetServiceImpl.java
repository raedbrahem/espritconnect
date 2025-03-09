package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.TrajetRepository;

import java.util.List;

@Service
public class TrajetServiceImpl implements IServiceTrajet {

    private final TrajetRepository trajetRepository;

    @Autowired
    public TrajetServiceImpl(TrajetRepository trajetRepository) {
        this.trajetRepository = trajetRepository;
    }

    @Override
    public List<Trajet> retrieveAllTrajets() {
        return trajetRepository.findAll();
    }

    @Override
    public Trajet retrieveTrajet(Long id_trajet) {
        return trajetRepository.findById(id_trajet).orElse(null);
    }

    @Override
    public Trajet addTrajet(Trajet trajet) {
        return trajetRepository.save(trajet);
    }

    @Override
    public void removeTrajet(Long id_trajet) {
        trajetRepository.deleteById(id_trajet);
    }

    @Override
    public Trajet modifyTrajet(Trajet trajet) {
        return trajetRepository.save(trajet);
    }
}
