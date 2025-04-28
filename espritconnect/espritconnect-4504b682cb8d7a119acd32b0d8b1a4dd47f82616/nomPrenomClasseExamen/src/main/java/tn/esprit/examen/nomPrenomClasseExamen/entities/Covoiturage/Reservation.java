package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reservation;


    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnore
    private User etudiant;

    private int nombrePlacesReservees;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateReservation;


    @Enumerated(EnumType.STRING)
    private EtatReservation etat;



    @ManyToOne
    @JsonBackReference
    Trajet trajet;

    @JsonManagedReference
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Paiement paiement;



    private Long montant;


    private String telephone;
    @JsonProperty("id_trajet")
    public Long getIdTrajet() {
        return trajet != null ? trajet.getId_trajet() : null;
    }

    public Long getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(Long id_reservation) {
        this.id_reservation = id_reservation;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }


    public int getNombrePlacesReservees() {
        return nombrePlacesReservees;
    }


    public void setNombrePlacesReservees(int nombrePlacesReservees) {
        this.nombrePlacesReservees = nombrePlacesReservees;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public EtatReservation getEtat() {
        return etat;
    }

    public void setEtat(EtatReservation etat) {
        this.etat = etat;
    }

    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }
    public boolean reserverPlaces(int placesDemandées) {
        if (trajet.peutAccepterReservation(placesDemandées)) {
            this.nombrePlacesReservees = placesDemandées;
            trajet.getReservations().add(this);
            trajet.updateAvailability();
            return true;  // Réservation acceptée ✅
        } else {
            return false; // Réservation refusée ❌
        }
    }
}
