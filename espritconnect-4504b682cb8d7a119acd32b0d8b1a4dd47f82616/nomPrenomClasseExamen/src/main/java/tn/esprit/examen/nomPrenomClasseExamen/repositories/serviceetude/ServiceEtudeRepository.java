package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;

public interface ServiceEtudeRepository extends JpaRepository<Service_Etude, Long> {
}
