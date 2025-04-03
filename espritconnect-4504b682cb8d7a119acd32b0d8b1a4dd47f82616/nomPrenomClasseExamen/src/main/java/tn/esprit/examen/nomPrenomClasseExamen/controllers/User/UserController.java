package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.RegisterRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setMotDePasse(request.getMotDePasse());
        user.setTelephone(request.getTelephone());
        user.setNiveauEtude(request.getNiveauEtude());
        user.setAdresse(request.getAdresse());
        user.setPhotoProfil(request.getPhotoProfil());
        user.setCarteEtudiant(request.getCarteEtudiant());
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER); // Rôle par défaut : USER si non spécifié

        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Ceci est un endpoint réservé aux admins !");
    }
}
