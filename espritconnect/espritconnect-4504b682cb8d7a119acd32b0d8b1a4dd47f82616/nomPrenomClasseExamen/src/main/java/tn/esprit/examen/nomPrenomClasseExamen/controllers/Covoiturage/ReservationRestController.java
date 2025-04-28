package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.EtatReservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.TrajetRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServiceReservation;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.ReservationServiceImpl;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.StripeServiceImpl;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.TrajetServiceImpl;

import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
@Slf4j
public class ReservationRestController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationRestController.class);

    @Autowired
    private IServiceReservation serviceReservation;
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationServiceImpl reservationRepository;
    @Autowired
    private TrajetRepository trajetRepository;
    @Autowired
    private StripeServiceImpl stripeService;
    @Autowired
    private ReservationServiceImpl reservationService;
    @Autowired
    private TrajetServiceImpl trajetServiceImpl;


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


    @PostMapping("/add-reservation/{id_trajet}")
    public ResponseEntity<Reservation> addReservation(@PathVariable("id_trajet") Long idTrajet,
                                                      @RequestBody Reservation reservation) {
        try {
            // Récupérer l'utilisateur authentifié à partir du contexte de sécurité
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername(); // L'email est utilisé comme identifiant unique

            // Récupérer l'utilisateur à partir de l'email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Associer l'utilisateur (étudiant) à la réservation
            reservation.setEtudiant(user);

            // Récupérer le trajet associé à l'ID du trajet
            Trajet trajet = trajetRepository.findById(idTrajet)
                    .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

            // Associer le trajet à la réservation
            reservation.setTrajet(trajet);

            // Vérifier et définir l'état de la réservation
            if (reservation.getEtat() == null) {
                reservation.setEtat(EtatReservation.EN_ATTENTE); // Valeur par défaut si l'état est null
            }

            // Vérifier et définir la date de réservation si elle est null
            if (reservation.getDateReservation() == null) {
                reservation.setDateReservation(LocalDateTime.now()); // Définir la date actuelle
            }

            // Calculer le montant total de la réservation (double)
            double montantTotal = trajet.getMontant() * reservation.getNombrePlacesReservees();

            // Convertir le montant en Long (arrondi ou conversion explicite)
            long montantLong = Math.round(montantTotal); // Arrondir le montant pour le convertir en Long

            // Définir le montant dans la réservation
            reservation.setMontant(montantLong);

            // Appeler le service pour ajouter la réservation
            Reservation newReservation = serviceReservation.addReservation(reservation, idTrajet);
            logger.info("Réservation ajoutée avec succès : {}", newReservation);

            return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la réservation : {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    // http://localhost:8089/reservation/remove-reservation/{reservation-id}
    @DeleteMapping("/remove-reservation/{reservationId}")
    @Transactional
    public ResponseEntity<Void> removeReservation(@PathVariable("reservationId") Long reservationId) {
        try {
            log.info("🔵 Tentative de suppression de la réservation ID: {}", reservationId);

            Reservation reservation = serviceReservation.retrieveReservation(reservationId);

            if (reservation == null) {
                log.warn("❌ Réservation avec ID {} non trouvée.", reservationId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // 🔥 Récupérer et mettre à jour le trajet lié
            Trajet trajet = reservation.getTrajet();
            if (trajet != null) {
                int nouvellesPlaces = trajet.getPlacesDisponibles() + reservation.getNombrePlacesReservees();
                nouvellesPlaces = Math.min(4, Math.max(0, nouvellesPlaces)); // Toujours entre 0 et 4
                trajet.setPlacesDisponibles(nouvellesPlaces);

                trajet.getReservations().remove(reservation); // Important : retirer de la liste

                trajetServiceImpl.modifyTrajet(trajet); // Sauvegarder le trajet modifié
            }

            // ✅ Supprimer directement la réservation
            serviceReservation.removeReservation(reservationId);

            log.info("✅ Réservation ID {} supprimée avec succès.", reservationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (EntityNotFoundException e) {
            log.error("❌ Réservation ID {} non trouvée: {}", reservationId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de la réservation ID {}: {}", reservationId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:808/reservation/modify-reservation
    @PutMapping("/modify-reservation")
    public ResponseEntity<Reservation> modifyReservation(@RequestBody Reservation reservation) {
        try {
            logger.info("Tentative de modification de la réservation avec ID : {}", reservation.getId_reservation());
            Reservation updatedReservation = serviceReservation.modifyReservation(reservation);
            logger.info("Réservation avec ID {} modifiée avec succès", reservation.getId_reservation());
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la réservation avec ID {} : {}", reservation.getId_reservation(), e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // http://localhost:8089/tpfoyer/reservation/user-payments/{reservationId}
    @GetMapping("/user-payments/{reservationId}")
    public ResponseEntity<List<Paiement>> getUserPayments(@PathVariable Long reservationId) {
        List<Paiement> payments = paiementRepository.findByReservationId(reservationId);
        if (payments.isEmpty()) {
            logger.info("Aucun paiement trouvé pour la réservation ID {}", reservationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Pas de paiements trouvés
        }
        logger.info("Paiements trouvés pour la réservation ID {} : {}", reservationId, payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/byTrajet/{id_trajet}")
    public List<Reservation> getAllReservationsByTrajetId(@PathVariable Long id_trajet) {
        return serviceReservation.getAllReservationsByTrajetId(id_trajet);
    }


    @GetMapping("/current-user-email")
    public ResponseEntity<String> getCurrentUserEmail() {
        // Récupérer l'utilisateur authentifié
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // L'email est utilisé comme identifiant unique

        // Vérifier si l'utilisateur existe
        if (email != null && !email.isEmpty()) {
            return new ResponseEntity<>(email, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Si l'utilisateur n'est pas authentifié


        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/reservationMontant/{id_reservation}")
    public ResponseEntity<Long> getMontantByReservationId(@PathVariable Long id_reservation) {
        try {
            // Récupérer la réservation par son ID
            Reservation reservation = reservationService.retrieveReservation(id_reservation);

            // Retourner uniquement le montant
            return ResponseEntity.ok(reservation.getMontant());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myReservations")
    public ResponseEntity<List<Reservation>> getCurrentUserReservations() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Reservation> reservations = serviceReservation.getAllReservationByUserId(currentUser.getId());

            if (reservations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des réservations de l'utilisateur courant: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des réservations: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getReservationStats() {
        return ResponseEntity.ok(reservationService.getReservationStats());
    }

    @GetMapping("/stats/daily")
    public ResponseEntity<?> getDailyStats() {
        Map<String, Object> stats = reservationService.getReservationStats();
        return ResponseEntity.ok(stats.get("dailyEvolution"));
    }

    @GetMapping("/stats/top-trajets")
    public ResponseEntity<?> getTopTrajets() {
        Map<String, Object> stats = reservationService.getReservationStats();
        return ResponseEntity.ok(stats.get("topTrajets"));
    }

    @GetMapping("/stats/by-state")
    public ResponseEntity<?> getStatsByState() {
        Map<String, Object> stats = reservationService.getReservationStats();
        return ResponseEntity.ok(stats.get("reservationsByState"));
    }
}
