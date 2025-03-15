package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;

import java.util.List;

public interface ServiceEtudeRepository extends JpaRepository<Service_Etude, Long> {

    @Query("SELECT s.subject AS subject, COUNT(s) AS count " +
            "FROM Service_Etude s GROUP BY s.subject")
    List<Object[]> countBySubject();
}