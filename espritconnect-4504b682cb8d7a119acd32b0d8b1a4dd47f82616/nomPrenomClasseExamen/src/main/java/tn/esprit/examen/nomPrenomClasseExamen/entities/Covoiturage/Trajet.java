package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime date_depart;
    private LocalDateTime date_arrivee;
    private float prix;
    private int placesDisponibles;  // Nombre de places restantes
    @Enumerated(EnumType.STRING)
    private TypeTrajet typeTrajet;
    @ManyToOne(cascade = CascadeType.ALL)
    private User conducteur;
    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reservation> reservations;
    private boolean estDisponible;
    public void updateAvailability() {
        // Si le nombre de places disponibles est 0, rendre le trajet indisponible
        if (placesDisponibles == 0) {
            estDisponible = false;
        } else {
            estDisponible = true;
        }
    }// True si des places sont disponibles


}
