package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
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

    private String motDePasse;
    private String niveauEtude;
    private String photoProfil;
    private Date dateInscription;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String statutVerification;
    private String telephone;

    // Les utilisateurs que cet utilisateur suit
    @ManyToMany
    @JoinTable(
            name = "user_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followee_id")
    )
    @JsonIgnore
    private Set<User> followees = new HashSet<>();

    // Les utilisateurs qui suivent cet utilisateur
    @ManyToMany(mappedBy = "followees")
    @JsonIgnore
    private Set<User> followers = new HashSet<>();


}
