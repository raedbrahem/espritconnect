package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.rating_etude;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.rating_etudeRepository;

import java.util.List;

@Service
public class RatingEtudeServiceImpl implements IRating_etudeService {

    @Autowired
    private rating_etudeRepository ratingEtudeRepository;

    @Override
    public rating_etude addrating(rating_etude rating) {
        return ratingEtudeRepository.save(rating);
    }

    @Override
    public void deleterating(rating_etude rating) {
        ratingEtudeRepository.delete(rating);
    }

    @Override
    public rating_etude updaterating(rating_etude rating) {
        return ratingEtudeRepository.save(rating);
    }

    @Override
    public List<rating_etude> getAllratings() {
        return ratingEtudeRepository.findAll();
    }

    @Override
    public rating_etude retrieverating(Long id) {
        return ratingEtudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + id));
    }
}
