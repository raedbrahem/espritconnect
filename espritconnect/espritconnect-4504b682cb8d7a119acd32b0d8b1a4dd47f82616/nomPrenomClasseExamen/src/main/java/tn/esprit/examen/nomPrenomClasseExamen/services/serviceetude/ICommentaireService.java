package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;

import java.util.List;

public interface ICommentaireService {
    Commentaire addCommentaire(Commentaire commentaire);
    void deleteCommentaire(Commentaire commentaire);
    Commentaire updateCommentaire(Commentaire commentaire);
    List<Commentaire> getAllCommentaires();
    Commentaire retrieveCommentaire(Long id);
    List<Commentaire> getCommentairesByServiceEtudeId(Long serviceEtudeId);
}
