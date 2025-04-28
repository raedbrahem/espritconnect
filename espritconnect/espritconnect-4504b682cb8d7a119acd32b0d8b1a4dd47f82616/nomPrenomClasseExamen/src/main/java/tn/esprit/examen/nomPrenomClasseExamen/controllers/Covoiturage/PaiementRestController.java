package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.ReservationServiceImpl;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.StripeServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class PaiementRestController {

    @Autowired
    private ReservationServiceImpl reservationService;
    @Autowired
    private PaiementRepository paiementRepository;// Le service qui récupère la réservation
    @Autowired
    private StripeServiceImpl stripeServiceImpl;

    @Autowired
    public PaiementRestController(ReservationServiceImpl reservationService,
                                  PaiementRepository paiementRepository) {
        this.reservationService = reservationService;
        this.paiementRepository = paiementRepository;
    }



    @PostMapping("/create-payment-intent/{id_reservation}")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@PathVariable Long id_reservation) {
        try {
            // 1. Récupération de la réservation
            Reservation reservation = reservationService.findById(id_reservation)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation introuvable"));

            // 2. Utilisation DIRECTE du montant de la réservation (déjà calculé)
            Long montant = reservation.getMontant(); // Ne plus multiplier ici

            // 3. Création/mise à jour du paiement
            Paiement paiement = paiementRepository.findByReservationId(id_reservation)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Paiement newPaiement = new Paiement();
                        newPaiement.setReservation(reservation);
                        newPaiement.setMoyenPaiement("CARTEBANCAIRE");
                        return newPaiement;
                    });

            paiement.setMontant(montant); // Stocke le montant exact

            // 4. Création du PaymentIntent
            PaymentIntent intent = PaymentIntent.create(
                    PaymentIntentCreateParams.builder()
                            .setAmount(montant) // Utilisation du montant direct
                            .setCurrency("usd")
                            .putMetadata("reservation_id", id_reservation.toString())
                            .build()
            );

            // 5. Sauvegarde
            paiement.setPaymentIntentId(intent.getId());
            paiementRepository.save(paiement);

            // 6. Retour
            return ResponseEntity.ok(Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "amount", montant, // Montant exact
                    "currency", "usd"
            ));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur Stripe: " + e.getMessage()));
        }
    }
    @GetMapping("/paiements-effectues")
    public ResponseEntity<List<Map<String, Object>>> getPaiementsEffectues() {
        try {
            List<Paiement> paiements = paiementRepository.findByStatusOrderByCreatedAtDesc("pending");

            List<Map<String, Object>> result = paiements.stream()
                    .map(paiement -> {
                        Map<String, Object> paiementMap = new HashMap<>();
                        paiementMap.put("paiement_id", paiement.getId_paiement());
                        paiementMap.put("created_at", paiement.getCreatedAt());
                        paiementMap.put("montant", paiement.getMontant());
                        paiementMap.put("reservation_id", paiement.getReservation() != null ?
                                paiement.getReservation().getId_reservation() : null);
                        paiementMap.put("etudiant_id", paiement.getUtilisateur() != null ?
                                paiement.getUtilisateur().getId() : null);
                        return paiementMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePaiement(@PathVariable Long id) {
        try {
            stripeServiceImpl.removePaiementById(id);
            return ResponseEntity.ok().body("Paiement supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
