package tn.esprit.examen.nomPrenomClasseExamen.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Recherche sur le nom et l'email
    List<User> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nomKeyword, String emailKeyword);
}
