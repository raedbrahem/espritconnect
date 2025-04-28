package tn.esprit.examen.nomPrenomClasseExamen.config.bidding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BidWebSocketHandler extends TextWebSocketHandler {

    // In-memory storage for active products and bids
    private final Map<Long, Product> activeProducts = new ConcurrentHashMap<>();
    private final Map<Long, Bid> currentHighestBids = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // For broadcasting messages

    public BidWebSocketHandler(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse the message sent by the client to extract bid information
        String payload = message.getPayload();
        String[] bidData = payload.split(",");
        Long productId = Long.valueOf(bidData[0]);
        BigDecimal bidAmount = new BigDecimal(bidData[1]);

        // Retrieve the product being bid on
        Product product = activeProducts.get(productId);

        // Get the authenticated user's name (bidderName) from Spring Security
        String bidderName = SecurityContextHolder.getContext().getAuthentication().getName(); // Get the authenticated user's username (bidderName)

        // Fetch the User object for the bidder
        var bidder = userRepository.findByEmail(bidderName).orElse(null);

        // If the product exists and the bidding deadline has not passed
        if (product != null && LocalDateTime.now().isBefore(product.getDeadline())) {
            // Retrieve the current highest bid for the product
            Bid currentBid = currentHighestBids.get(productId);

            // Check if the new bid is higher than the current highest bid
            if (currentBid == null || bidAmount.compareTo(currentBid.getAmount()) > 0) {
                // Create a new bid with the User object
                Bid newBid = new Bid(product, bidder, bidAmount, LocalDateTime.now());
                currentHighestBids.put(productId, newBid);

                // Broadcast the updated bid to all clients subscribed to the product (for real-time updates)
                String bidUpdateMessage = "New bid placed: " + bidder.getNom() + " bid " + bidAmount;
                messagingTemplate.convertAndSend("/topic/product/" + productId, bidUpdateMessage);  // This is where the message is sent to the topic
            } else {
                // If the new bid is not higher, inform the user
                session.sendMessage(new TextMessage("Your bid is not higher than the current bid."));
            }
        } else if (product != null && LocalDateTime.now().isAfter(product.getDeadline())) {
            // If the bidding period has expired, inform the user
            session.sendMessage(new TextMessage("Bidding for this product has ended."));
        } else {
            // If the product doesn't exist, inform the user
            session.sendMessage(new TextMessage("Product not found."));
        }
    }
}
