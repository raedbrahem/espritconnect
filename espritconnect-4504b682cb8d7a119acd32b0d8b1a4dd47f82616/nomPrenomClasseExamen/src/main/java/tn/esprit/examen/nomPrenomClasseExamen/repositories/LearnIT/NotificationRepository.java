package tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Notificationn;

@Repository

public interface NotificationRepository extends JpaRepository<Notificationn, Long> {
}
