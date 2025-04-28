package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Notification;

import java.util.List;

public interface NotificationRepositoryy extends JpaRepository<Notification, Long> {

    // Méthodes personnalisées si nécessaire
    List<Notification> findByUserIdOrderByDateCreationDesc(Long userId);
    List<Notification> findByUser_IdAndLueFalse(Long userId);
    List<Notification> findByUser_IdOrderByDateCreationDesc(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.reservation.id_reservation = :reservationId")
    void deleteByReservationId(@Param("reservationId") Long reservationId);



}
