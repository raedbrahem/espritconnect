package tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Check if an order exists for a product
     * @param productId The product ID to check
     * @return True if an order exists for the product, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.product.idProduct = :productId")
    boolean existsByProductId(@Param("productId") Long productId);
}
