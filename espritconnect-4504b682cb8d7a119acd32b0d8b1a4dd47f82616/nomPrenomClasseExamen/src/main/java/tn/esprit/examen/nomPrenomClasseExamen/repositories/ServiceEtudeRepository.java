package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;

public interface ServiceEtudeRepository extends JpaRepository<Service_Etude, Long> {
}
