package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Commentaire;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;

import java.util.List;

public interface ICommentaireService {
    Commentaire addCommentaire(Commentaire commentaire);
    void deleteCommentaire(Commentaire commentaire);
    Commentaire updateCommentaire(Commentaire commentaire);
    List<Commentaire> getAllCommentaires();
    Commentaire retrieveCommentaire(Long id);
}
