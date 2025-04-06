package tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query to fetch products with seller's email
    @Query("SELECT p FROM Product p JOIN FETCH p.seller s WHERE p.seller.id = :sellerId")
    List<Product> findProductsBySellerId(@Param("sellerId") Long sellerId);
    Optional<Product> findById(Long id);
    @Query("SELECT p.idProduct FROM Product p WHERE p.idProduct = :id")
    Long findProductIdById(@Param("id") Long id);



}
