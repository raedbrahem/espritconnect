package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.rating_etude;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IRating_etudeService;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/rating")
@RestController
public class RatingEtudeRestController {

    @Autowired
    private final IRating_etudeService ratingEtudeService;

    private final UserService userService;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/add")
    public rating_etude addRating(@RequestBody rating_etude rating) {
        User user = getAuthenticatedUser();
        rating.setUser(user);
        return ratingEtudeService.addrating(rating);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRating(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        rating_etude rating = ratingEtudeService.retrieverating(id);

        if (!rating.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this rating.");
        }

        ratingEtudeService.deleterating(rating);
    }

    @PutMapping("/update/{id}")
    public rating_etude updateRating(@PathVariable("id") Long id, @RequestBody rating_etude rating) {
        User user = getAuthenticatedUser();
        rating_etude existingRating = ratingEtudeService.retrieverating(id);

        if (!existingRating.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this rating.");
        }

        rating.setId(id);
        rating.setUser(user); // ensure user stays the same
        return ratingEtudeService.updaterating(rating);
    }

    @GetMapping("/all")
    public List<rating_etude> getAllRatings() {
        return ratingEtudeService.getAllratings();
    }

    @GetMapping("/retrieve/{id}")
    public rating_etude retrieveRating(@PathVariable Long id) {
        return ratingEtudeService.retrieverating(id);
    }

    @GetMapping("/by-service-etude/{serviceEtudeId}")
    public List<rating_etude> getRatingsByServiceEtudeId(@PathVariable Long serviceEtudeId) {
        return ratingEtudeService.getRatingsByServiceEtudeId(serviceEtudeId);
    }
}
