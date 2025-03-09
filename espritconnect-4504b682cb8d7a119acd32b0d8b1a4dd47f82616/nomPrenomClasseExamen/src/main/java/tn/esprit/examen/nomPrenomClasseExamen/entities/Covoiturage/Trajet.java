package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;
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
    @Enumerated(EnumType.STRING)
    private TypeTrajet typeTrajet;
    @ManyToOne(cascade = CascadeType.ALL)
    User user;
    @OneToOne(mappedBy = "trajet", cascade = CascadeType.ALL)
    private Paiement paiement;


}
