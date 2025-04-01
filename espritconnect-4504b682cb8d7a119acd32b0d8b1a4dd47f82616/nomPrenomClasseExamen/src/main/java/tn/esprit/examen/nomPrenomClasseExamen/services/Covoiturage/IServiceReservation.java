package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;

import java.util.List;

public interface IServiceReservation {
    List<Reservation> retrieveAllReservations();
    Reservation retrieveReservation(Long id_reservation); // Utilisation de Long au lieu de long
    Reservation addReservation(Reservation reservation);
    void removeReservation(Long id_reservation); // Utilisation de Long au lieu de long
    Reservation modifyReservation(Reservation reservation);
}
