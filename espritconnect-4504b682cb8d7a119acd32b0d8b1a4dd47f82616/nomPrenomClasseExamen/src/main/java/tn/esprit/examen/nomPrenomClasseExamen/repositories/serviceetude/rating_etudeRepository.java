package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.rating_etude;

import java.util.List;

public interface rating_etudeRepository extends JpaRepository<rating_etude, Long> {
    @Query("SELECT r.serviceEtude.tutor.id AS tutorId, AVG(r.stars) AS avgRating " +
            "FROM rating_etude r GROUP BY r.serviceEtude.tutor.id")
    List<Object[]> findAverageRatingPerTutor();
}
