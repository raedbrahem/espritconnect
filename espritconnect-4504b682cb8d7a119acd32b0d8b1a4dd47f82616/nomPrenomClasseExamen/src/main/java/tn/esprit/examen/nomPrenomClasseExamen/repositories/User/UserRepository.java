package tn.esprit.examen.nomPrenomClasseExamen.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by email (returns Optional to handle the case when no user is found)
    Optional<User> findByEmail(String email);

    // Search users by name or email, ignoring case (returns a list of matching users)
    List<User> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nomKeyword, String emailKeyword);
}
