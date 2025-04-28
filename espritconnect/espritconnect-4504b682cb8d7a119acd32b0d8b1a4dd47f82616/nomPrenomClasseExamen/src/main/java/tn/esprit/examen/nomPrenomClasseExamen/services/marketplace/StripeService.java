package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.PaymentStatus;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a Stripe customer for a user
     * @param user The user to create a customer for
     * @return The Stripe customer ID
     * @throws StripeException If there's an error creating the customer
     */
    public String createCustomer(User user) throws StripeException {
        // Check if user already has a Stripe customer ID
        if (user.getStripeCustomerId() != null && !user.getStripeCustomerId().isEmpty()) {
            return user.getStripeCustomerId();
        }

        // Create a new customer in Stripe
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getNom() + " " + user.getPrenom())
                .setEmail(user.getEmail())
                .setPhone(user.getTelephone())
                .build();

        Customer customer = Customer.create(params);

        // Update user with Stripe customer ID
        user.setStripeCustomerId(customer.getId());
        userRepository.save(user);

        return customer.getId();
    }

    /**
     * Create a payment intent for an order
     * @param order The order to create a payment intent for
     * @param buyer The buyer making the payment
     * @param seller The seller receiving the payment
     * @return The created payment
     * @throws StripeException If there's an error creating the payment intent
     */
    public Payment createPaymentIntent(Order order, User buyer, User seller) throws StripeException {
        // Ensure buyer has a Stripe customer ID
        if (buyer.getStripeCustomerId() == null || buyer.getStripeCustomerId().isEmpty()) {
            createCustomer(buyer);
        }

        // Convert BigDecimal to cents (Stripe uses cents)
        long amountInCents = order.getFinalPrice().multiply(new BigDecimal("100")).longValue();

        // Create payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setCustomer(buyer.getStripeCustomerId())
                .setDescription("Payment for order #" + order.getIdOrder())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setBuyer(buyer);
        payment.setSeller(seller);
        payment.setAmount(order.getFinalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Confirm a payment intent
     * @param paymentIntentId The payment intent ID to confirm
     * @return The updated payment
     * @throws StripeException If there's an error confirming the payment intent
     */
    public Payment confirmPayment(String paymentIntentId) throws StripeException {
        // Retrieve payment from database
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentIntentId));

        // Retrieve payment intent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Update payment status based on payment intent status
        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setUpdatedAt(LocalDateTime.now());

            // Transfer funds to seller if they have a Stripe account ID
            if (payment.getSeller().getStripeAccountId() != null && !payment.getSeller().getStripeAccountId().isEmpty()) {
                transferToSeller(payment);
            }
        } else if ("canceled".equals(paymentIntent.getStatus())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    /**
     * Transfer funds to seller
     * @param payment The payment to transfer funds for
     * @return The updated payment
     * @throws StripeException If there's an error transferring funds
     */
    public Payment transferToSeller(Payment payment) throws StripeException {
        // Check if seller has a Stripe account ID
        User seller = payment.getSeller();
        if (seller.getStripeAccountId() == null || seller.getStripeAccountId().isEmpty()) {
            throw new IllegalArgumentException("Seller does not have a Stripe account ID");
        }

        // Convert BigDecimal to cents (Stripe uses cents)
        long amountInCents = payment.getAmount().multiply(new BigDecimal("100")).longValue();

        // Create transfer
        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setDestination(seller.getStripeAccountId())
                .setDescription("Transfer for order #" + payment.getOrder().getIdOrder())
                .build();

        Transfer transfer = Transfer.create(params);

        // Update payment
        payment.setStripeTransferId(transfer.getId());
        payment.setStatus(PaymentStatus.TRANSFERRED_TO_SELLER);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Handle a webhook event from Stripe
     * @param payload The webhook payload
     * @param sigHeader The signature header
     * @param endpointSecret The endpoint secret
     * @throws StripeException If there's an error handling the webhook
     */
    public void handleWebhook(String payload, String sigHeader, String endpointSecret) throws StripeException {
        if (endpointSecret == null || endpointSecret.isEmpty()) {
            // For testing purposes, if no endpoint secret is provided, just log the payload
            System.out.println("Received webhook payload: " + payload);
            return;
        }

        try {
            // Verify the webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Get the event type
            String eventType = event.getType();
            System.out.println("Received Stripe webhook event: " + eventType);

            // Handle different event types
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                // Add more event types as needed
                default:
                    System.out.println("Unhandled event type: " + eventType);
            }
        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.err.println("Invalid webhook signature: " + e.getMessage());
            throw new RuntimeException("Invalid webhook signature", e);
        }
    }

    /**
     * Handle payment_intent.succeeded event
     * @param event The Stripe event
     */
    private void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
            String paymentIntentId = paymentIntent.getId();

            try {
                // Update payment status
                confirmPayment(paymentIntentId);
                System.out.println("Payment succeeded for payment intent: " + paymentIntentId);
            } catch (Exception e) {
                System.err.println("Error confirming payment: " + e.getMessage());
            }
        } else {
            System.err.println("Error deserializing payment intent");
        }
    }

    /**
     * Handle payment_intent.payment_failed event
     * @param event The Stripe event
     */
    private void handlePaymentIntentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
            String paymentIntentId = paymentIntent.getId();

            try {
                // Find payment by payment intent ID
                Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                        .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentIntentId));

                // Update payment status
                payment.setStatus(PaymentStatus.FAILED);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                System.out.println("Payment failed for payment intent: " + paymentIntentId);
            } catch (Exception e) {
                System.err.println("Error updating failed payment: " + e.getMessage());
            }
        } else {
            System.err.println("Error deserializing payment intent");
        }
    }
}
