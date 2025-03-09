package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

public class LoginRequest {
    private String email;
    private String motDePasse;

    public LoginRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
