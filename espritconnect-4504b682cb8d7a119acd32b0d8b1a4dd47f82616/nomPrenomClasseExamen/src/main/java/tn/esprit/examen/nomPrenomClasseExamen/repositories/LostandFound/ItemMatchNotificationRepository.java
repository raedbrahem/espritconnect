package tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.ItemMatchNotification;

import java.util.List;

public interface ItemMatchNotificationRepository extends JpaRepository<ItemMatchNotification, Long> {
    List<ItemMatchNotification> findByIsValidatedFalse(); // pending matches
    List<ItemMatchNotification> findByRecipientIdOrderByCreatedAtDesc(Long userId);

}
