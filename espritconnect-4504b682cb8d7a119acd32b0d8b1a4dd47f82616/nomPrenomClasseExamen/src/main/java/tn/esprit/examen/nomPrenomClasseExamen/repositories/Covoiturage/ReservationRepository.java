package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
