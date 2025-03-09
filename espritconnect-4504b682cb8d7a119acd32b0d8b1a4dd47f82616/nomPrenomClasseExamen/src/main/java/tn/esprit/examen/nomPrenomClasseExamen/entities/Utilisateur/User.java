package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;

import java.util.Date;
import java.util.List;

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
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Service_Etude> serviceEtudes;
}
