// src/main/java/tn/esprit/examen/nomPrenomClasseExamen/controllers/User/ProfilController.java
package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.StorageService;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

@RestController
@RequestMapping("/api/users")
public class ProfilController {

    private final UserService userService;
    private final StorageService storageService;

    @Autowired
    public ProfilController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Utilisation de POST pour la mise à jour multipart
    @PostMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateProfile(
            @RequestPart("user") String userJson, // <- on récupère le JSON en String
            @RequestPart(value = "photoProfil", required = false) MultipartFile photoProfil,
            @RequestPart(value = "carteEtudiant", required = false) MultipartFile carteEtudiant,
            Authentication authentication) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        User updatedUser = objectMapper.readValue(userJson, User.class); // <- désérialisation manuelle

        System.out.println("User authentifié : " + authentication.getName());

        if (photoProfil != null && !photoProfil.isEmpty()) {
            String photoUrl = storageService.storeFile(photoProfil);
            updatedUser.setPhotoProfil(photoUrl);
        }
        if (carteEtudiant != null && !carteEtudiant.isEmpty()) {
            String carteUrl = storageService.storeFile(carteEtudiant);
            updatedUser.setCarteEtudiant(carteUrl);
        }

        User savedUser = userService.updateUser(updatedUser.getId(), updatedUser);
        return ResponseEntity.ok(savedUser);
    }

}
