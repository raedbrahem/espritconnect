package tn.esprit.examen.nomPrenomClasseExamen.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.PasswordResetToken;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // Nouvelle méthode pour trouver un token par utilisateur
    Optional<PasswordResetToken> findByUser(User user);
}
