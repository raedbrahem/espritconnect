package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Proof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_proof;

    @ManyToOne
    @JsonIgnore
    User proprietaire;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id_item")
    private Item item;

    // Optional: link to matched lost item

    String image_url;
    String description;
    LocalDateTime dateSubmitted;
    Double similarityScore; // <-- AI confidence of match
    boolean validated; // <-- True if ownership is verified and item was matched
    String name;
    String lieuPerdu;


}
