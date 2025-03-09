package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String niveauEtude;
    private String adresse;
    private String photoProfil;
    private String carteEtudiant;
    private Role role; // Nouveau champ pour le rôle
}
