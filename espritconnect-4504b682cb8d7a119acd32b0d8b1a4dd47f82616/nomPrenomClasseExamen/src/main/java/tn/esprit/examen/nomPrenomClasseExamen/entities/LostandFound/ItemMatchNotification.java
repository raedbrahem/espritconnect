package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemMatchNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private boolean seen;
    private LocalDateTime createdAt;
    private double SimilarityScore;
    @JsonIgnore
    @ManyToOne
    private User recipient;

    private boolean isValidated;
    private Boolean isMatchAccepted; // null = not reviewed, true/false = reviewed

    @OneToOne
    private Proof proof;

    @ManyToOne
    private Item matchedItem;

    private LocalDateTime matchedAt;


}
