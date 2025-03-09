package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_item;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    User proprietaire;

    @Column(nullable = false)
    String item_name;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    String lieuPerdu;

    @Column(nullable = false)
    LocalDate datePerdu;

    @Column(nullable = false)
    boolean retrouve;

    @Column(nullable = false)
    LocalDateTime datePublication_item;
}

