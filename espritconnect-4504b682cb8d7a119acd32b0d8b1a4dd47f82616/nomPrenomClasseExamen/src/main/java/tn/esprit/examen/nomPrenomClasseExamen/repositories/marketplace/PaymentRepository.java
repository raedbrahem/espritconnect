package tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);
    
    List<Payment> findBySellerId(Long sellerId);
    
    List<Payment> findByBuyerId(Long buyerId);
    
    Optional<Payment> findByOrder(Order order);
}
