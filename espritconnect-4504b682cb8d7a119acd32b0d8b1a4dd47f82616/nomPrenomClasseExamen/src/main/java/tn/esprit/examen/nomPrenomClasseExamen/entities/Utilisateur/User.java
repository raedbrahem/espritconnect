package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

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
}
