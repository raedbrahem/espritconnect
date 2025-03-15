package tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;

import java.util.List;

public interface ITutoringRepository extends JpaRepository<TutoringEvent, Long> {

    @Query(value = "SELECT t.tutor_id AS tutorId, COUNT(t.id) AS sessionCount " +
            "FROM tutoring_event t GROUP BY t.tutor_id", nativeQuery = true)
    List<Object[]> countSessionsByTutor();

    @Query(value = "SELECT t.tutor_id AS tutorId, " +
            "SUM(TIMESTAMPDIFF(HOUR, t.start_time, t.end_time)) AS totalHours " +
            "FROM tutoring_event t GROUP BY t.tutor_id", nativeQuery = true)
    List<Object[]> sumTutoringHoursByTutor();

    @Query(value = "SELECT HOUR(t.start_time) AS hour, COUNT(*) AS sessionCount " +
            "FROM tutoring_event t GROUP BY HOUR(t.start_time)", nativeQuery = true)
    List<Object[]> countSessionsByTimeSlot();
}