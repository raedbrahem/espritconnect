package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
}