package tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUser(User user);

}
