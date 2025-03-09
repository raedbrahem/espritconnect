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
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_paiement;
    @Enumerated(EnumType.STRING)
    private MoyenPaiement moyenPaiement;
    private LocalDateTime date_transaction;
    @OneToOne
    @JoinColumn(name = "id_trajet", nullable = false)
    private Trajet trajet;
    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private User user;

}
