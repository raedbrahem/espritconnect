package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Notificationn;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Vote;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@JsonIgnoreProperties({"questions", "answers", "notifications", "votes", "followees", "followers", "serviceEtudes", "serviceEtudesProvided"})
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

    private String motDePasse;
    private String niveauEtude;
    private String photoProfil;
    private Date dateInscription;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String statutVerification;
    private String telephone;

    // Stripe related fields
    private String stripeCustomerId; // For buyers to make payments
    private String stripeAccountId;  // For sellers to receive payments

    // The users that this user follows
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followee_id")
    )
    @JsonIgnore
    private Set<User> followees = new HashSet<>();


    // The users who follow this user
    @ManyToMany(mappedBy = "followees")
    @JsonIgnore
    private Set<User> followers = new HashSet<>();

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    public Set<Question> questions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    public Set<Answer> answers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    public Set<Notificationn> notifications;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    public Set<Vote> votes;

}
