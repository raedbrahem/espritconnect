package tn.esprit.examen.nomPrenomClasseExamen.controllers.Foyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.ReservationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.IFoyerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.ReservationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.ReservationFService;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/reservationF")
public class ReservationFController {

    @Autowired
    private ReservationFService reservationService;

    @Autowired
    private ReservationFRepository reservationRepo;

    @Autowired
    private IFoyerRepository foyerRepo ;

    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }
        return authentication.getName();
    }

    @PostMapping("/{foyerId}/demander")
    public ResponseEntity<?> demanderReservation(
            @PathVariable Long foyerId,
            @RequestBody ReservationRequest request) {
        try {
            String email = getCurrentUserEmail();
            User userConnecte = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            ReservationF reservation = reservationService.creerReservation(
                    foyerId, userConnecte.getId(),
                    request.getDateDebut(), request.getDateFin(), request.getMessageDemande()
            );
            return ResponseEntity.ok(reservation);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur : " + e.getMessage());
        }
    }

    @PutMapping("/{reservationId}/accepter")
    public ResponseEntity<?> accepterReservation(@PathVariable Long reservationId) {
        try {
            String email = getCurrentUserEmail();
            User proprietaire = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            ReservationF reservation = reservationService.accepterReservation(reservationId, proprietaire.getId());

            reservation.setFoyer(null);
            reservation.setDemandeur(null);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{reservationId}/refuser")
    public ResponseEntity<?> refuserReservation(@PathVariable Long reservationId) {
        try {
            String email = getCurrentUserEmail();
            User proprietaire = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            reservationService.refuserReservation(reservationId, proprietaire.getId());

            return ResponseEntity.ok("Réservation refusée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{reservationId}/annuler")
    public ResponseEntity<?> annulerReservation(@PathVariable Long reservationId) {
        try {
            String email = getCurrentUserEmail();
            User proprietaire = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            reservationService.annulerReservation(reservationId, proprietaire.getId());
            return ResponseEntity.ok("Réservation annulée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mes-reservations-foyer")
    public ResponseEntity<List<ReservationF>> getReservationsForUserFoyers() {
        String email = getCurrentUserEmail();
        User proprietaire = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

        return ResponseEntity.ok(reservationService.getReservationsForUserFoyers(proprietaire.getId()));
    }


    @GetMapping("/mes-reservations")
    public ResponseEntity<List<ReservationF>> getReservationsAsDemandeur() {
        try {
            String email = getCurrentUserEmail();
            User userConnecte = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            List<ReservationF> reservations = reservationService.getReservationsParDemandeur(userConnecte.getId());
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de vos réservations", e);
        }
    }



    @GetMapping("/reservations-par-foyer/{foyerId}")
    public ResponseEntity<List<ReservationF>> getReservationsByFoyer(@PathVariable Long foyerId) {
        try {
            List<ReservationF> reservations = reservationRepo.findByFoyerId(foyerId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des réservations pour ce foyer", e);
        }
    }



    @DeleteMapping("/{reservationId}/supprimer")
    public ResponseEntity<?> supprimerReservation(@PathVariable Long reservationId) {
        try {
            String email = getCurrentUserEmail();
            User demandeur = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            reservationService.supprimerParDemandeur(reservationId, demandeur.getId());
            return ResponseEntity.ok("Réservation supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public static class ReservationRequest {
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String messageDemande;

        public LocalDate getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
        public LocalDate getDateFin() { return dateFin; }
        public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
        public String getMessageDemande() { return messageDemande; }
        public void setMessageDemande(String messageDemande) { this.messageDemande = messageDemande; }
    }
}
