package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.BidRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;

@Service
public class BidService {
    @Autowired
    private ProductRepository productRepository;

    // In-memory storage for the highest bid per product
    private final Map<Long, Bid> currentHighestBids = new ConcurrentHashMap<>();

    // Save bid (In-memory storage)
    public Bid saveBid(Bid bid) {
        // Store the bid in memory, you can later check if it's the highest and update accordingly
        currentHighestBids.put(bid.getProduct().getIdProduct(), bid);
        return bid; // return the saved bid (in memory)
    }

    // Get the highest bid for a product from memory
    public Bid getHighestBidForProduct(Product product) {
        return currentHighestBids.get(product.getIdProduct()); // get from the in-memory map
    }

    // Method to check and update highest bid (based on the bid amount)
    public void updateHighestBidForProduct(Long productId, Bid savedBid) {
        // Update the highest bid for this product in memory
        currentHighestBids.put(productId, savedBid);
    }

    // This method is to simulate checking and updating the buyer when auction expires
    public void checkAndUpdateExpiredProducts() {
        // Loop through all active bids and check for expired auctions, update the buyer if needed
        currentHighestBids.forEach((productId, highestBid) -> {
            Product product = highestBid.getProduct();

            // Only process if the deadline has passed and no buyer has been assigned yet
            if (product.getDeadline().isBefore(LocalDateTime.now())) {

                // If there is a valid bid (greater than or equal to the asking price), assign it as the buyer
                    product.setBuyer(highestBid.getBidder()); // Set the highest bidder as the buyer
                    product.setItemState(false);  // Mark product as sold
                    productRepository.save(product); // Save the updated product
                    System.out.println("Product " + product.getName() + " sold to " + highestBid.getBidder().getNom());
                }

        });
    }

}
