package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

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
    private User etudiant;
    @ManyToOne
    private Trajet trajet;
    private int nombrePlacesReservees;
    private LocalDateTime dateReservation;
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL,orphanRemoval = true)
    private Paiement paiement;
    @Enumerated(EnumType.STRING)
    private EtatReservation etat;

}
