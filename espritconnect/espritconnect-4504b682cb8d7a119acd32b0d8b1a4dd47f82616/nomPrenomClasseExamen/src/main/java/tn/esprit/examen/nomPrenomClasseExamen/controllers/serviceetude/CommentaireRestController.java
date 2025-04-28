package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.ICommentaireService;
import org.springframework.security.core.Authentication;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/commentaire")
@RestController
public class CommentaireRestController {

    @Autowired
    private final ICommentaireService serviceCommentaire;

    private final UserService userService;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/add")
    public Commentaire addCommentaire(@RequestBody Commentaire commentaire) {
        User user = getAuthenticatedUser(); // Ensure user is authenticated
        commentaire.setUser(user); // Set the comment author
        return serviceCommentaire.addCommentaire(commentaire);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCommentaire(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        Commentaire commentaire = serviceCommentaire.retrieveCommentaire(id);

        System.out.println("Logged in user ID: " + user.getId());
        System.out.println("Comment owner ID: " + commentaire.getUser().getId());

        if (!commentaire.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this comment.");
        }

        serviceCommentaire.deleteCommentaire(commentaire);
    }


    @PutMapping("/update/{id}")
    public Commentaire updateCommentaire(@PathVariable Long id, @RequestBody Commentaire commentaire) {
        User user = getAuthenticatedUser();
        Commentaire existingCommentaire = serviceCommentaire.retrieveCommentaire(id);

        if (!existingCommentaire.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this comment.");
        }

        commentaire.setId(id);
        commentaire.setUser(user); // Ensure the author remains the same
        return serviceCommentaire.updateCommentaire(commentaire);
    }

    @GetMapping("/all")
    public List<Commentaire> getAllCommentaires() {
        return serviceCommentaire.getAllCommentaires();
    }

    @GetMapping("/retrieve/{id}")
    public Commentaire retrieveCommentaire(@PathVariable Long id) {
        return serviceCommentaire.retrieveCommentaire(id);
    }

    @GetMapping("/by-service-etude/{serviceEtudeId}")
    public List<Commentaire> getCommentairesByServiceEtudeId(@PathVariable Long serviceEtudeId) {
        return serviceCommentaire.getCommentairesByServiceEtudeId(serviceEtudeId);
    }
}
