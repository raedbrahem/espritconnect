package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.EtatReservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.ReservationRepository;

@RestController
@RequestMapping("/api/payments")
public class PaiementtWebhookController {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final String stripeWebhookSecret;


    @Autowired
    public PaiementtWebhookController(PaiementRepository paiementRepository,
                                      ReservationRepository reservationRepository,
                                      @Value("${stripe.webhook.secret}") String stripeWebhookSecret) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
        this.stripeWebhookSecret = stripeWebhookSecret;
    }

    /*@PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalStateException("PaymentIntent deserialization failed"));

                // Récupérer le paiement à partir de l'ID du PaymentIntent
                Paiement paiement = paiementRepository.findBySessionId(intent.getId())
                        .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

                paiement.setStatus("succeeded");
                paiementRepository.save(paiement);

                // Mettre à jour la réservation liée
                Reservation reservation = paiement.getReservation();
                reservation.setEtat(EtatReservation.CONFIRME);
                reservationRepository.save(reservation);
            }

            return ResponseEntity.ok("Webhook handled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing webhook: " + e.getMessage());
        }
    }*/
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Vérifie la signature du webhook avec Stripe
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            // Vérifie si l'événement est de type "payment_intent.succeeded"
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalStateException("PaymentIntent deserialization failed"));

                // Récupérer l'ID de PaymentIntent et le montant payé
                String paymentIntentId = intent.getId();
                Long amountReceived = intent.getAmountReceived();
                String paymentMethod = intent.getPaymentMethodTypes().get(0); // Par exemple, "CARTEBANCAIRE"

                // Récupérer le paiement en base avec l'ID de la session (sessionId dans ton payload)
                Paiement paiement = paiementRepository.findBySessionId(intent.getId())
                        .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

                // Mettre à jour les informations du paiement
                paiement.setStatus("SUCCES");  // Le paiement a été réussi
                paiement.setMontant(amountReceived / 100);  // Stripe retourne en centimes
                paiement.setMoyenPaiement(paymentMethod);
                paiement.setPaymentIntentId(paymentIntentId);

                paiementRepository.save(paiement);  // Enregistrer les données dans la base

                // Mettre à jour la réservation associée
                Reservation reservation = paiement.getReservation();
                reservation.setEtat(EtatReservation.CONFIRME);  // Statut de réservation confirmé
                reservationRepository.save(reservation);  // Sauvegarder la réservation mise à jour
            }

            // Réponse 200 si tout est traité avec succès
            return ResponseEntity.ok("Webhook handled successfully");
        } catch (Exception e) {
            // Retourne une erreur si un problème survient
            return ResponseEntity.badRequest().body("Error processing webhook: " + e.getMessage());
        }
    }

}
