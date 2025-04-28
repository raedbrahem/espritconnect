package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.threeten.bp.LocalDateTime;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.NotificationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.ItemMatchNotification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String adresse;
    private String carteEtudiant;

    @Column(unique = true)
    private String email;
    private String stripeCustomerId;
    private String stripeAccountId;
    private String motDePasse;
    private String niveauEtude;
    private String photoProfil;
    private Date dateInscription;

    @Column(name = "code_verification")
    private String codeVerification;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String statutVerification;
    private String telephone;
    //Asma
    private String fcmToken;




    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Service_Etude> serviceEtudes;

    // New ManyToMany relationship with Service_Etude
    @ManyToMany
    @JoinTable(
            name = "user_service_etude",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "service_etude_id")
    )
    @JsonIgnore
    private List<Service_Etude> serviceEtudesProvided;

    public List<Service_Etude> getServiceEtudesProvided() {
        return serviceEtudesProvided;
    }

    public void setServiceEtudesProvided(List<Service_Etude> serviceEtudesProvided) {
        this.serviceEtudesProvided = serviceEtudesProvided;
    }

    ////foued///////////
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
   // @JsonIgnoreProperties(value = {"user", "answers", "question"})
    private Set<Question> questions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   // @JsonIgnoreProperties(value = {"user", "question"})
    @JsonIgnore
    private Set<Answer> answers;


    /// / LOST & FOUND

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Item> items;

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Proof> uploadedProofs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ItemMatchNotification> matchNotifications;

//////EYA//////

    @OneToMany(mappedBy = "conducteur", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trajet> trajets;


    // Relation avec les réservations où l'utilisateur est étudiant
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservations;


    /// salma////
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Foyer> foyers;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore  // Ignorer la sérialisation de cette collection
    public Set<NotificationF> notificationsF;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Preference preference;

}
