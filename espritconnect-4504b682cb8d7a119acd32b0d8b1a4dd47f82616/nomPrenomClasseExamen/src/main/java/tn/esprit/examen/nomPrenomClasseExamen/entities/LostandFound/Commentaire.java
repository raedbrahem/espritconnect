package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_item;

    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    User auteur;

    @Column(nullable = false, length = 500)
    String contenu;

    @Column(nullable = false)
    LocalDateTime datePublication;

    @ManyToOne
    @JoinColumn(name = "lost_item_id", nullable = false)
    Item lostItem;  // Linking comments directly to LostItem
}
