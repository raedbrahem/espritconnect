package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.trajet.id_trajet = :id_trajet")
    public List<Reservation> findByTrajetId(@Param("id_trajet") Long id_trajet);

    List<Reservation> findByEtudiantId(Long etudiantId);


    @Query("SELECT FUNCTION('DATE', r.dateReservation) as date, COUNT(r) as count, SUM(r.montant) as total " +
            "FROM Reservation r " +
            "GROUP BY FUNCTION('DATE', r.dateReservation) " +
            "ORDER BY date")
    List<Object[]> countReservationsByDay();

    // Statistique 2: Réservations par heure pour une date spécifique
    @Query("SELECT FUNCTION('HOUR', r.dateReservation) as hour, COUNT(r) as count " +
            "FROM Reservation r " +
            "WHERE FUNCTION('DATE', r.dateReservation) = FUNCTION('DATE', :date) " +
            "GROUP BY FUNCTION('HOUR', r.dateReservation)")
    List<Object[]> countReservationsByHour(LocalDateTime date);

    // Statistique 3: Répartition par état
    @Query("SELECT r.etat as state, COUNT(r) as count FROM Reservation r GROUP BY r.etat")
    List<Object[]> countReservationsByState();

    // Statistique 4: Top trajets demandés
    @Query("SELECT t.id_trajet as trajetId, COUNT(r) as count " +
            "FROM Reservation r JOIN r.trajet t " +
            "GROUP BY t.id_trajet " +
            "ORDER BY count DESC")
    List<Object[]> findPopularTrajets();


    // Méthode utilitaire pour le revenue total
    @Query("SELECT COALESCE(SUM(r.montant), 0) FROM Reservation r")
    Long getTotalRevenue();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.trajet WHERE r.id_reservation = :id")
    Optional<Reservation> findByIdWithTrajet(@Param("id") Long id);
}
