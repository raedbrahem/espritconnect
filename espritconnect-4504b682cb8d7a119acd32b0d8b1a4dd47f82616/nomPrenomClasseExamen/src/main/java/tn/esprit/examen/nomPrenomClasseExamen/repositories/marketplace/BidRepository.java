package tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace;


import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;

public interface BidRepository extends JpaRepository<Bid, Long> {
    // You can add custom query methods here if needed, like fetching bids by productId
    Bid findTopByProductOrderByAmountDesc(Product product);

}
