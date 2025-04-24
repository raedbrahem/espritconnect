package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean correctCategory;
    private String explanation;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
