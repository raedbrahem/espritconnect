package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;

import java.util.List;
import java.util.Map;

public interface ITutoringRepository extends JpaRepository<TutoringEvent, Long> {
    @Query("SELECT CONCAT(t.tutor.nom, ' ', t.tutor.prenom) AS tutorName, " +
            "COUNT(t.id) AS sessionCount " +
            "FROM TutoringEvent t GROUP BY t.tutor.nom, t.tutor.prenom")
    List<Object[]> countSessionsByTutor();

    @Query("SELECT CONCAT(t.tutor.nom, ' ', t.tutor.prenom) AS tutorName, " +
            "SUM(FUNCTION('TIMESTAMPDIFF', HOUR, t.startTime, t.endTime)) AS totalHours " +
            "FROM TutoringEvent t GROUP BY t.tutor.nom, t.tutor.prenom")
    List<Object[]> sumTutoringHoursByTutor();

    @Query(value = "SELECT HOUR(t.start_time) AS hour, COUNT(*) AS sessionCount " +
            "FROM tutoring_event t GROUP BY HOUR(t.start_time)", nativeQuery = true)
    List<Object[]> countSessionsByTimeSlot();
}