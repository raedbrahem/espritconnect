package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_paiement;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "id_reservation")
    private Reservation reservation;

    private String sessionId;
    private String paymentIntentId;
    private Long montant;
    private String status; // "pending", "succeeded", "failed"

    private LocalDateTime createdAt;

    private String MoyenPaiement;

    public String getMoyenPaiement() {
        return MoyenPaiement;
    }



    public void setMoyenPaiement(String moyenPaiement) {
        MoyenPaiement = moyenPaiement;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "etudiant_id")  // Ce champ stocke l'ID de l'utilisateur (User)
    private User utilisateur;


    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }

    public Paiement(Reservation reservation, String sessionId, Long montant) {
        this.reservation = reservation;
        this.sessionId = sessionId;
        this.montant = reservation.getMontant() * reservation.getNombrePlacesReservees(); // Montant total    }
        this.utilisateur = utilisateur;
    }
}

