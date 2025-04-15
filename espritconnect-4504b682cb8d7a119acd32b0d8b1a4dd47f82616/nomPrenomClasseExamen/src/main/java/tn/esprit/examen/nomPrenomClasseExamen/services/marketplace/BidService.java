package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.BidRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.OrderRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BidService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    /**
     * Check for expired products, update buyers, and create orders automatically
     */
    public void checkAndUpdateExpiredProducts() {
        // First, check in-memory bids
        currentHighestBids.forEach((productId, highestBid) -> {
            Product product = highestBid.getProduct();

            // Only process if the deadline has passed and the product is still active
            if (product.getDeadline().isBefore(LocalDateTime.now()) && product.isItemState()) {
                // Set the highest bidder as the buyer
                product.setBuyer(highestBid.getBidder());
                product.setItemState(false);  // Mark product as sold
                Product savedProduct = productRepository.save(product);

                // Create an order automatically
                createOrderForProduct(savedProduct, highestBid.getAmount());

                System.out.println("Product " + product.getName() + " sold to " + highestBid.getBidder().getNom() + ". Order created automatically.");
            }
        });

        // Also check database for any products that have expired but don't have orders yet
        List<Product> expiredProducts = productRepository.findByDeadlineBeforeAndBuyerIsNotNullAndSellerIsNotNull(LocalDateTime.now());

        for (Product product : expiredProducts) {
            // Check if an order already exists for this product
            if (!orderRepository.existsByProductId(product.getIdProduct())) {
                // Find the highest bid for this product from the database
                Bid highestBid = bidRepository.findTopByProductOrderByAmountDesc(product);
                BigDecimal finalPrice = (highestBid != null) ? highestBid.getAmount() : product.getAskingPrice();

                // Create an order for this product
                createOrderForProduct(product, finalPrice);

                System.out.println("Order created for previously expired product: " + product.getName());
            }
        }
    }

    /**
     * Create an order for a product that has been sold
     * @param product The product to create an order for
     * @param finalPrice The final price of the product
     * @return The created order
     */
    private Order createOrderForProduct(Product product, BigDecimal finalPrice) {
        // Check if an order already exists for this product
        if (orderRepository.existsByProductId(product.getIdProduct())) {
            System.out.println("Order already exists for product: " + product.getName());
            return null;
        }

        // Create a new order
        Order order = new Order();
        order.setProduct(product);
        order.setFinalPrice(finalPrice);
        order.setOrderDate(LocalDateTime.now());

        // Save the order
        return orderRepository.save(order);
    }
}
