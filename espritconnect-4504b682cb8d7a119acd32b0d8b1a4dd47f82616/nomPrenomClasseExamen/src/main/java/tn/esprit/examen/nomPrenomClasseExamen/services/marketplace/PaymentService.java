package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.PaymentStatus;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.OrderRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.PaymentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StripeService stripeService;

    /**
     * Initiate a payment for an order
     * @param orderId The order ID to initiate payment for
     * @param buyerId The buyer ID making the payment
     * @return The created payment
     * @throws StripeException If there's an error creating the payment intent
     */
    @Transactional
    public Payment initiatePayment(Long orderId, Long buyerId) throws StripeException {
        // Retrieve order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Retrieve buyer
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found with ID: " + buyerId));

        // Retrieve seller from the product in the order
        User seller = order.getProduct().getSeller();
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found for order: " + orderId);
        }

        // Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrder(order);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        // Create payment intent
        return stripeService.createPaymentIntent(order, buyer, seller);
    }

    /**
     * Confirm a payment
     * @param paymentId The payment ID to confirm
     * @return The updated payment
     * @throws StripeException If there's an error confirming the payment
     */
    @Transactional
    public Payment confirmPayment(Long paymentId) throws StripeException {
        // Retrieve payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

        // Confirm payment intent
        return stripeService.confirmPayment(payment.getStripePaymentIntentId());
    }

    /**
     * Transfer funds to seller
     * @param paymentId The payment ID to transfer funds for
     * @return The updated payment
     * @throws StripeException If there's an error transferring funds
     */
    @Transactional
    public Payment transferToSeller(Long paymentId) throws StripeException {
        // Retrieve payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

        // Check if payment is completed
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment must be completed before transferring funds");
        }

        // Transfer funds to seller
        return stripeService.transferToSeller(payment);
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payment by ID
     * @param id The payment ID
     * @return The payment
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
    }

    /**
     * Get payments by seller ID
     * @param sellerId The seller ID
     * @return List of payments for the seller
     */
    public List<Payment> getPaymentsBySellerId(Long sellerId) {
        return paymentRepository.findBySellerId(sellerId);
    }

    /**
     * Get payments by buyer ID
     * @param buyerId The buyer ID
     * @return List of payments for the buyer
     */
    public List<Payment> getPaymentsByBuyerId(Long buyerId) {
        return paymentRepository.findByBuyerId(buyerId);
    }
}
