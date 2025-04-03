package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByServiceEtudeId(Long serviceEtudeId);
}