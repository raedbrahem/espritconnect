package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ProjetDetail;

import java.util.Optional;

public interface ProjetDetailRepository extends JpaRepository<ProjetDetail, Long> {
}
