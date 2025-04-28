package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.TrajetRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.List;
import java.util.Optional;


@Service
public class TrajetServiceImpl implements IServiceTrajet {

    private final TrajetRepository trajetRepository;
    ;
    private static final Logger logger = LoggerFactory.getLogger(TrajetServiceImpl.class);


    @Autowired
    public TrajetServiceImpl(TrajetRepository trajetRepository) {
        this.trajetRepository = trajetRepository;

    }
    @Transactional
    @Override
    public List<Trajet> retrieveAllTrajets() {
        // Log the start of the method execution
        logger.info("Fetching all trajets from the repository");

        // Retrieve all trajets
        List<Trajet> trajets = trajetRepository.findAll();

        // Log the result of the fetch
        logger.info("Number of trajets retrieved: {}", trajets.size());

        // Return the list of trajets
        return trajets;
    }

    @Transactional
    @Override
    public Trajet retrieveTrajet(Long id_trajet) {
        Trajet trajet = trajetRepository.findById(id_trajet).orElse(null);
        if (trajet != null) {
            // Force initialization of related entities
            Hibernate.initialize(trajet.getConducteur().getAnswers());
        }
        return trajet;
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
    @Override
    public List<Trajet> getTrajetsByUserId(Long userId) {
        return trajetRepository.findTrajetsByUserId(userId);

    }


}
