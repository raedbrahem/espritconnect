package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.ReservationRepository;
import java.util.List;

@Service
public class ReservationServiceImpl implements IServiceReservation {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation retrieveReservation(Long id_reservation) {
        return reservationRepository.findById(id_reservation).orElse(null);
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        Trajet trajet = reservation.getTrajet();

        // Vérifier si le trajet a assez de places
        if (trajet.getPlacesDisponibles() < reservation.getNombrePlacesReservees()) {
            throw new IllegalStateException("Nombre de places insuffisant pour cette réservation");
        }

        // Déduire le nombre de places réservées
        trajet.setPlacesDisponibles(trajet.getPlacesDisponibles() - reservation.getNombrePlacesReservees());

        // Mettre à jour la disponibilité du trajet
        trajet.updateAvailability();

        return reservationRepository.save(reservation);
    }

    @Override
    public void removeReservation(Long id_reservation) {
        Reservation reservation = reservationRepository.findById(id_reservation).orElse(null);

        if (reservation != null) {
            Trajet trajet = reservation.getTrajet();

            // Réajouter les places libérées
            trajet.setPlacesDisponibles(trajet.getPlacesDisponibles() + reservation.getNombrePlacesReservees());

            // Mettre à jour la disponibilité du trajet
            trajet.updateAvailability();

            // Supprimer la réservation
            reservationRepository.deleteById(id_reservation);
        }
    }

    @Override
    public Reservation modifyReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

}
