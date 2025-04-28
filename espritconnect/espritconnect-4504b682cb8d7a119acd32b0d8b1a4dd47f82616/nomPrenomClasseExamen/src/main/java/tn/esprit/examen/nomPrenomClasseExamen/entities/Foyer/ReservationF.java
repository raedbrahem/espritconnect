package tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class ReservationF {

    public enum StatutReservation {
        EN_ATTENTE,
        CONFIRMEE,
        ANNULEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demandeur_id", nullable = false)
    @JsonIgnore
    @JsonIgnoreProperties({"notifications", "questions", "votes", "answers", "notificationsF", "followers", "followees", "serviceEtudes", "serviceEtudesProvided", "foyers", "preference"})
    private User demandeur;

    @ManyToOne(fetch = FetchType.EAGER) // ⛳ pour forcer l'inclusion du foyer
    @JoinColumn(name = "foyer_id", nullable = false)
    @JsonIgnoreProperties({"user", "reservations", "notifications"})
    private Foyer foyer;


    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private String messageDemande;

    @PrePersist
    private void validateDates() {
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }
    }
}
