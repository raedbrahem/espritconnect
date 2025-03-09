package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notification;
    private String message;
    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;
    @Enumerated(EnumType.STRING)
    private EtatNotification etatNotification;
    @ManyToOne(cascade = CascadeType.ALL)
    User user;
}
