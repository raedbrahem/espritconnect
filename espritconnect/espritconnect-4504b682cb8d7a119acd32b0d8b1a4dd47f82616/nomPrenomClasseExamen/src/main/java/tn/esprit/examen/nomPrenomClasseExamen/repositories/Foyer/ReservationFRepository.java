package tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.ReservationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.ReservationF.StatutReservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationFRepository extends JpaRepository<ReservationF, Long> {

    @Query("SELECT r FROM ReservationF r WHERE " +
            "r.foyer.id = :foyerId AND " +
            "r.statut = 'CONFIRMEE' AND " +
            "((r.dateDebut BETWEEN :debut AND :fin) OR " +
            "(r.dateFin BETWEEN :debut AND :fin) OR " +
            "(:debut BETWEEN r.dateDebut AND r.dateFin) OR " +
            "(:fin BETWEEN r.dateDebut AND r.dateFin))")
    List<ReservationF> findConflictingReservations(
            @Param("foyerId") Long foyerId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    @Query("SELECT r FROM ReservationF r JOIN r.foyer f WHERE " +
            "f.user.id = :proprietaireId AND " +
            "r.statut = :statut")
    List<ReservationF> findByFoyerUserIdAndStatut(
            @Param("proprietaireId") Long proprietaireId,
            @Param("statut") StatutReservation statut);

    List<ReservationF> findByDemandeurId(Long demandeurId);



    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM ReservationF r WHERE " +
            "r.foyer.id = :foyerId AND " +
            "r.statut = 'CONFIRMEE' AND " +
            "((r.dateDebut BETWEEN :debut AND :fin) OR " +
            "(r.dateFin BETWEEN :debut AND :fin) OR " +
            "(:debut BETWEEN r.dateDebut AND r.dateFin) OR " +
            "(:fin BETWEEN r.dateDebut AND r.dateFin))")
    boolean existsConflictingReservation(
            @Param("foyerId") Long foyerId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);




    List<ReservationF> findByFoyerId(Long foyerId);
    List<ReservationF> findByFoyerIdAndStatut(Long foyerId, ReservationF.StatutReservation statut);

    List<ReservationF> findByFoyerUserId(@Param("userId") Long userId);


    @Query("SELECT r FROM ReservationF r JOIN r.foyer f WHERE f.user.id = :userId")
    List<ReservationF> findByFoyerOwnerId(@Param("userId") Long userId);


}