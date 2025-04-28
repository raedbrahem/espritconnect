package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    @Query("SELECT p FROM Paiement p WHERE p.reservation.id_reservation = :id_reservation")
    public List<Paiement> findByReservationId(@Param("id_reservation") Long id_reservation);

    Optional<Paiement> findBySessionId(String sessionId);
    Optional<Paiement> findByPaymentIntentId(String paymentIntentId);

    List<Paiement> findByStatusOrderByCreatedAtDesc(String status);

}

