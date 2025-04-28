package tn.esprit.examen.nomPrenomClasseExamen.controllers.marketplace;

import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.PaymentService;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.StripeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeService stripeService;

    @Value("${stripe.public.key}")
    private String stripePublishableKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    /**
     * Get Stripe publishable key
     * @return The Stripe publishable key
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publishableKey", stripePublishableKey);
        return ResponseEntity.ok(config);
    }

    /**
     * Initiate a payment for an order
     * @param orderId The order ID to initiate payment for
     * @param buyerId The buyer ID making the payment
     * @return The created payment
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestParam Long orderId, @RequestParam Long buyerId) {
        try {
            Payment payment = paymentService.initiatePayment(orderId, buyerId);

            // Create response with client secret for frontend
            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("clientSecret", payment.getStripePaymentIntentId());
            response.put("amount", payment.getAmount());
            response.put("status", payment.getStatus());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating payment: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Confirm a payment
     * @param paymentId The payment ID to confirm
     * @return The updated payment
     */
    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.confirmPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error confirming payment: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Transfer funds to seller
     * @param paymentId The payment ID to transfer funds for
     * @return The updated payment
     */
    @PostMapping("/transfer/{paymentId}")
    public ResponseEntity<?> transferToSeller(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.transferToSeller(paymentId);
            return ResponseEntity.ok(payment);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error transferring funds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * Get payment by ID
     * @param id The payment ID
     * @return The payment
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Get payments by seller ID
     * @param sellerId The seller ID
     * @return List of payments for the seller
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Payment>> getPaymentsBySellerId(@PathVariable Long sellerId) {
        return ResponseEntity.ok(paymentService.getPaymentsBySellerId(sellerId));
    }

    /**
     * Get payments by buyer ID
     * @param buyerId The buyer ID
     * @return List of payments for the buyer
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<Payment>> getPaymentsByBuyerId(@PathVariable Long buyerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBuyerId(buyerId));
    }

    /**
     * Handle webhook events from Stripe
     * @param payload The webhook payload
     * @param sigHeader The signature header
     * @return Success message
     */
    @PostMapping("/marketplace-webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            stripeService.handleWebhook(payload, sigHeader, webhookSecret);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}
