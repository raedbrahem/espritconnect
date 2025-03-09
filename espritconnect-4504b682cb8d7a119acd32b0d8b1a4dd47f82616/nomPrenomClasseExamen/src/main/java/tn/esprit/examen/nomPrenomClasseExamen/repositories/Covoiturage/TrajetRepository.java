package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si nécessaire
}
