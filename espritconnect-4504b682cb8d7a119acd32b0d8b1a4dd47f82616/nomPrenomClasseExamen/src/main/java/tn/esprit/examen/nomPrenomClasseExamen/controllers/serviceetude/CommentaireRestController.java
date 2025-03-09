package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.ICommentaireService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/commentaire")
@RestController
public class CommentaireRestController {
    @Autowired
    private final ICommentaireService serviceCommentaire;

    @PostMapping("/add")
    public Commentaire addCommentaire(@RequestBody Commentaire commentaire) {
        return serviceCommentaire.addCommentaire(commentaire);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCommentaire(@PathVariable Long id) {
        Commentaire commentaire = serviceCommentaire.retrieveCommentaire(id);
        serviceCommentaire.deleteCommentaire(commentaire);
    }

    @PutMapping("/update/{id}")
    public Commentaire updateCommentaire(
            @PathVariable("id") Long id,
            @RequestBody Commentaire commentaire) {
        commentaire.setId(id); // Set the ID from the URL to the request body
        return serviceCommentaire.updateCommentaire(commentaire); // Use the service to update the entity
    }

    @GetMapping("/all")
    public List<Commentaire> getAllCommentaires() {
        return serviceCommentaire.getAllCommentaires();
    }

    @GetMapping("/retrieve/{id}")
    public Commentaire retrieveCommentaire(@PathVariable Long id) {
        return serviceCommentaire.retrieveCommentaire(id);
    }
}
