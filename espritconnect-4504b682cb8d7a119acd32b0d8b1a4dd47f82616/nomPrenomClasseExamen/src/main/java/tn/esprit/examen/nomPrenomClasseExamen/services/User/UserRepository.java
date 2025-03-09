package tn.esprit.examen.nomPrenomClasseExamen.services.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Recherche des utilisateurs dont le nom ou l'email contient le mot-clé (sans tenir compte de la casse)
    List<User> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nomKeyword, String emailKeyword);
}
