package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.rating_etude;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IRating_etudeService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/rating")
@RestController
public class RatingEtudeRestController {

    @Autowired
    private final IRating_etudeService ratingEtudeService;

    @PostMapping("/add")
    public rating_etude addRating(@RequestBody rating_etude rating) {
        return ratingEtudeService.addrating(rating);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRating(@PathVariable Long id) {
        rating_etude rating = ratingEtudeService.retrieverating(id);
        ratingEtudeService.deleterating(rating);
    }

    @PutMapping("/update/{id}")
    public rating_etude updateRating(
            @PathVariable("id") Long id,
            @RequestBody rating_etude rating) {
        rating.setId(id); // Set the ID from the URL to the request body
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
}
