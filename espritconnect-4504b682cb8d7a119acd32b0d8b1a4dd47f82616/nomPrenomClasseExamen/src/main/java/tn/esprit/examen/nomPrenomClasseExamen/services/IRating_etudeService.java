package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.rating_etude;

import java.util.List;

public interface IRating_etudeService {
    rating_etude addrating(rating_etude commentaire);
    void deleterating(rating_etude commentaire);
    rating_etude updaterating(rating_etude commentaire);
    List<rating_etude> getAllratings();
    rating_etude retrieverating(Long id);
}
