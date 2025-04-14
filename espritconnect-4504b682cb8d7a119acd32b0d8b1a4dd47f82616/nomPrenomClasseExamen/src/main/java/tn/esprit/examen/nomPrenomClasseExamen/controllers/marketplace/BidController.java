package tn.esprit.examen.nomPrenomClasseExamen.controllers.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Bid;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.BidService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/bids")
public class BidController {

    private final BidService bidService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public BidController(BidService bidService, ProductRepository productRepository, UserRepository userRepository) {
        this.bidService = bidService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Bid placeBid(@RequestBody Bid bid, @RequestParam Long productId) {
        // Get the authenticated user (bidder)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String bidderName = authentication.getName(); // The username of the authenticated user

        // Retrieve the product being bid on
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Retrieve the user (bidder) using the username
        User bidder = userRepository.findByEmail(bidderName).orElse(null);
        if (bidder == null) {
            throw new RuntimeException("User not found");
        }

            // Ensure the bid is greater than or equal to the asking price
            if (bid.getAmount().compareTo(product.getAskingPrice()) < 0) {
                throw new RuntimeException("Bid amount must be greater than or equal to the asking price.");
            }

            // Retrieve the current highest bid for the product
            Bid currentHighestBid = bidService.getHighestBidForProduct(product);

            // Ensure the bid is higher than the current highest bid
            if (currentHighestBid != null && bid.getAmount().compareTo(currentHighestBid.getAmount()) <= 0) {
                throw new RuntimeException("Bid amount must be higher than the current highest bid.");
            }

            // Set the bidder and product for the bid
            bid.setBidder(bidder);  // Set the authenticated user as the bidder
            bid.setProduct(product);  // Set the product for this bid
            bid.setBidTime(LocalDateTime.now());  // Set the timestamp for the bid

            // Save the bid using the BidService (store in-memory)
            Bid savedBid = bidService.saveBid(bid);

            // Check if this bid is the highest and update it in-memory if necessary
            bidService.updateHighestBidForProduct(productId, savedBid);
            bidService.checkAndUpdateExpiredProducts();


        return savedBid;
        }



    @PutMapping("/updateBuyer/{productId}")
    public String updateProductBuyer(@PathVariable Long productId) {
        bidService.checkAndUpdateExpiredProducts();  // This checks if products are expired and updates the buyer accordingly
        return "Product buyer updated if expired bidding ended.";
    }

}
