package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;

import java.util.List;
import java.util.Map;

public interface IServiceReservation {
    List<Reservation> retrieveAllReservations();

    Reservation retrieveReservation(Long id_reservation); // Utilisation de Long au lieu de long

    Reservation addReservation(Reservation reservation, Long idTrajet);

    void removeReservation(Long id_reservation); // Utilisation de Long au lieu de long

    Reservation modifyReservation(Reservation reservation);

    List<Reservation> getAllReservationsByTrajetId(Long id_trajet);

    List<Reservation> getAllReservationByUserId(Long Id);


    Map<String, Object> getReservationStats();

}