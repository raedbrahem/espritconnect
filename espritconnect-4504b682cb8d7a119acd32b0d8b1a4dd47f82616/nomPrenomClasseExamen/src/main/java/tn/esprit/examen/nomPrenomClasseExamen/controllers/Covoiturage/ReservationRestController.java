package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServiceReservation;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationRestController {

    @Autowired
    private IServiceReservation serviceReservation;

    // http://localhost:8089/tpfoyer/reservation/retrieve-all-reservations
    @GetMapping("/retrieve-all-reservations")
    public List<Reservation> getReservations() {
        return serviceReservation.retrieveAllReservations();
    }

    // http://localhost:8089/tpfoyer/reservation/retrieve-reservation/{reservation-id}
    @GetMapping("/retrieve-reservation/{reservation-id}")
    public Reservation retrieveReservation(@PathVariable("reservation-id") Long reservation_id) {
        return serviceReservation.retrieveReservation(reservation_id);
    }

    // http://localhost:8089/tpfoyer/reservation/add-reservation
    @PostMapping("/add-reservation")
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        try {
            Reservation addedReservation = serviceReservation.addReservation(reservation);
            return new ResponseEntity<>(addedReservation, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // http://localhost:8089/tpfoyer/reservation/remove-reservation/{reservation-id}
    @DeleteMapping("/remove-reservation/{reservation-id}")
    public ResponseEntity<String> removeReservation(@PathVariable("reservation-id") Long reservation_id) {
        try {
            serviceReservation.removeReservation(reservation_id);
            return new ResponseEntity<>("Reservation deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // http://localhost:8089/tpfoyer/reservation/modify-reservation
    @PutMapping("/modify-reservation")
    public ResponseEntity<Reservation> modifyReservation(@RequestBody Reservation reservation) {
        Reservation updatedReservation = serviceReservation.modifyReservation(reservation);
        if (updatedReservation != null) {
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
