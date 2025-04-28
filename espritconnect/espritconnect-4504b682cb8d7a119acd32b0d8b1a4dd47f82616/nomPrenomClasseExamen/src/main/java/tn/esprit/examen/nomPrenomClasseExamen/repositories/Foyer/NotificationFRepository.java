package tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.NotificationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;

@Repository
public interface NotificationFRepository extends JpaRepository<NotificationF, Long> {

    // 📬 Récupère les notifications d’un utilisateur par son ID (triées par date décroissante)
    List<NotificationF> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 📥 Récupère les notifications non lues pour un utilisateur
    List<NotificationF> findByUser_IdAndLuFalse(Long userId);

    // (Facultatif) Récupère les notifications d’un utilisateur (par objet User)
    List<NotificationF> findByUser(User user);

    List<NotificationF> findByUserOrderByCreatedAtDesc(User user);
}
