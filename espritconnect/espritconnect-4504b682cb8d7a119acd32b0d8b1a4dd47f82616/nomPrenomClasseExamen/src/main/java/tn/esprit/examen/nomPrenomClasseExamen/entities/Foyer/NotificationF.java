package tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationF {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private Boolean lu = false;  // Indique si la notification a été lue ou non

    private LocalDateTime createdAt = LocalDateTime.now();


    @ManyToOne
    @JoinColumn(name = "foyer_id", nullable = true)
    private Foyer foyer;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Assurez-vous que l'utilisateur est toujours associé à la notification
    private User user;


}
