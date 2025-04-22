package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.rating_etude;

import java.util.List;
import java.util.Map;

public interface rating_etudeRepository extends JpaRepository<rating_etude, Long> {
    @Query("SELECT CONCAT(r.serviceEtude.tutor.nom, ' ', r.serviceEtude.tutor.prenom) AS tutorName, " +
            "AVG(r.stars) AS avgRating " +
            "FROM rating_etude r GROUP BY r.serviceEtude.tutor.nom, r.serviceEtude.tutor.prenom")
    List<Object[]> findAverageRatingPerTutor();

    List<rating_etude> findByServiceEtudeId(Long serviceEtudeId);
}
