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

    private double montant;

    @Enumerated(EnumType.STRING)
    private MoyenPaiement moyenPaiement;

    @Enumerated(EnumType.STRING)
    private StatutPaiement statutPaiement;

    private LocalDateTime date_transaction;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

}
