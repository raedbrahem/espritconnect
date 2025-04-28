package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trajet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_trajet;
    private String point_depart;
    private String point_arrivee;
    private int placesDisponibles;


    @Enumerated(EnumType.STRING)
    private TypeTrajet typeTrajet;


    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP) // This ensures both date and time are stored
    private Date dateDepart;
    @Temporal(TemporalType.TIMESTAMP) // This ensures both date and time are stored
    private Date dateArrivee;



    private Double departLat;
    private Double departLng;
    private Double arriveeLat;
    private Double arriveeLng;

    private String telephone;



    @ManyToOne
    @JoinColumn(name = "conducteur_id", nullable = false)
    @JsonIgnore
    private User conducteur;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Reservation> reservations;

    private boolean estDisponible;


    private Long montant;


    public Long getId_trajet() {
        return id_trajet;
    }

    public void setId_trajet(Long id_trajet) {
        this.id_trajet = id_trajet;
    }

    public String getPoint_depart() {
        return point_depart;
    }

    public void setPoint_depart(String point_depart) {
        this.point_depart = point_depart;
    }

    public String getPoint_arrivee() {
        return point_arrivee;
    }

    public void setPoint_arrivee(String point_arrivee) {
        this.point_arrivee = point_arrivee;
    }


    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public TypeTrajet getTypeTrajet() {
        return typeTrajet;
    }

    public void setTypeTrajet(TypeTrajet typeTrajet) {
        this.typeTrajet = typeTrajet;
    }

    public User getConducteur() {
        return conducteur;
    }

    public void setConducteur(User conducteur) {
        this.conducteur = conducteur;
    }

    public boolean isEstDisponible() {
        return estDisponible;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }


    public Date getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(Date dateDepart) {
        this.dateDepart = dateDepart;
    }

    public Date getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(Date dateArrivee) {
        this.dateArrivee = dateArrivee;
    }


    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }


    public void updateAvailability() {
        int placesRéservées = reservations.stream().mapToInt(Reservation::getNombrePlacesReservees).sum();
        this.estDisponible = placesRéservées < this.placesDisponibles;
    }

    public boolean peutAccepterReservation(int placesDemandées) {
        int placesRéservées = reservations.stream().mapToInt(Reservation::getNombrePlacesReservees).sum();
        return (placesRéservées + placesDemandées) <= this.placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        if (placesDisponibles < 0 || placesDisponibles > 4) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être entre 1 et 4.");
        }
        this.placesDisponibles = placesDisponibles;
    }


    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

/*
    public void updateAvailability() {
        int placesRéservées = reservations.stream().mapToInt(Reservation::getNombrePlacesReservees).sum();
        estDisponible = placesRéservées < placesDisponibles;
    }

*/


    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.typeTrajet = TypeTrajet.EN_ATTENTE; // Statut par défaut
        this.estDisponible = false; // Non disponible tant que non confirmé
    }

    public void confirmerTrajet() {
        if (this.typeTrajet == TypeTrajet.EN_ATTENTE) {
            this.typeTrajet = TypeTrajet.CONFIRME;
            this.estDisponible = true;
        }
    }

    public void annulerTrajet() {
        if (this.typeTrajet == TypeTrajet.EN_ATTENTE || this.typeTrajet == TypeTrajet.CONFIRME) {
            this.typeTrajet = TypeTrajet.ANNULE;
            this.estDisponible = false;
        }


    }
}
