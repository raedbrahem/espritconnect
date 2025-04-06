package tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace;

import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bid")
    private Long idBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private Product product; // The product being bid on

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)  // Join with user id (bidder)
    private User bidder;  // Reference to the user who placed the bid

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // The bid amount

    @Column(nullable = false)
    private LocalDateTime bidTime = LocalDateTime.now(); // The time the bid was placed

    // Constructor
    public Bid(Product product, User bidder, BigDecimal amount, LocalDateTime bidTime) {
        this.product = product;
        this.bidder = bidder;
        this.amount = amount;
        this.bidTime = bidTime;
    }

    public Bid() {

    }

    // Getters and Setters
    public Long getIdBid() { return idBid; }
    public void setIdBid(Long idBid) { this.idBid = idBid; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getBidder() { return bidder; }
    public void setBidder(User bidder) { this.bidder = bidder; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }


}
