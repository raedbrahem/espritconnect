package tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notification;
    private String message;
    private LocalDateTime dateCreation;
    private boolean lue;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "roles", "reservations", "votes", "emailVerified", "photoProfil", "adresse", "telephone", "dateInscription"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    @Transient
    private String nomUtilisateurReservation;
    @Transient
    private String prenomUtilisateurReservation;
    @Transient
    private String emailUtilisateurReservation;
    @Transient
    private String telephoneUtilisateurReservation;
    @PostLoad
    private void postLoad() {
        if (reservation != null && reservation.getEtudiant() != null) {
            User etudiant = reservation.getEtudiant();
            this.nomUtilisateurReservation = etudiant.getNom();
            this.prenomUtilisateurReservation = etudiant.getPrenom();
            this.emailUtilisateurReservation = etudiant.getEmail();
            this.telephoneUtilisateurReservation = etudiant.getTelephone();
        }
    }
}
