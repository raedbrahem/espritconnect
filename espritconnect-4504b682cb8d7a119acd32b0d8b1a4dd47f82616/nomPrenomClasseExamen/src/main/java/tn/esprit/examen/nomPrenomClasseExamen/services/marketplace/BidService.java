package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.BidRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BidService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BidRepository bidRepository; // Assuming you have a repository for persisting bids

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For WebSocket messaging

    // In-memory storage for the highest bid per product
    private final Map<Long, Bid> currentHighestBids = new ConcurrentHashMap<>();

    // Save bid (In-memory storage + Database persistence)
    public Bid saveBid(Bid bid) {
        // Persist the bid in the database
        bidRepository.save(bid);

        // Store the bid in memory
        currentHighestBids.put(bid.getProduct().getIdProduct(), bid);

        // Return the saved bid
        return bid;
    }

    // Get the highest bid for a product from memory
    public Bid getHighestBidForProduct(Product product) {
        return currentHighestBids.get(product.getIdProduct());
    }

    // Method to check and update highest bid (based on the bid amount)
    public void updateHighestBidForProduct(Long productId, Bid savedBid) {
        // Update the highest bid for this product in memory
        currentHighestBids.put(productId, savedBid);

        // Send real-time updates to WebSocket clients watching the product
        broadcastNewHighestBid(savedBid);
    }

    // Broadcast the new highest bid to all clients via WebSocket
    private void broadcastNewHighestBid(Bid savedBid) {
        Product product = savedBid.getProduct();
        String productName = product.getName();
        BigDecimal bidAmount = savedBid.getAmount();
        String message = "New highest bid for " + productName + ": " + bidAmount;

        // Send the update to all clients subscribed to the product's topic
        messagingTemplate.convertAndSend("/topic/bidding/" + product.getIdProduct(), message);
    }

    // This method is to simulate checking and updating the buyer when auction expires
    public void checkAndUpdateExpiredProducts() {
        // Loop through all active bids and check for expired auctions, update the buyer if needed
        currentHighestBids.forEach((productId, highestBid) -> {
            Product product = highestBid.getProduct();

            // Only process if the deadline has passed and no buyer has been assigned yet
            if (product.getDeadline().isBefore(LocalDateTime.now()) && product.isItemState()) {
                // If there is a valid bid (greater than or equal to the asking price), assign it as the buyer
                product.setBuyer(highestBid.getBidder()); // Set the highest bidder as the buyer
                product.setItemState(false);  // Mark product as sold
                productRepository.save(product); // Save the updated product

                System.out.println("Product " + product.getName() + " sold to " + highestBid.getBidder().getNom());
            }
        });
    }
}
