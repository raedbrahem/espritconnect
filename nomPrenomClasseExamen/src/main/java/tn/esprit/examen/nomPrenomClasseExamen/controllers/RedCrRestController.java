package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.RedCrescent;
import tn.esprit.examen.nomPrenomClasseExamen.services.IServiceRedCr;

import java.util.List;

@Tag(name = "Gestion Red Crescent")
@RestController
@AllArgsConstructor
@RequestMapping("/redcrescent")
public class RedCrRestController {

    @Autowired
    private IServiceRedCr redCrescentService;

    @GetMapping("/retrieve-all")
    public List<RedCrescent> getAllRedCrescents() {
        return redCrescentService.getredCrescentDetails();
    }

    @GetMapping("/retrieve/{id}")
    public RedCrescent getRedCrescent(@PathVariable("id") Long id) {
        return redCrescentService.retrieve(id);
    }

    @PostMapping("/add")
    public RedCrescent addRedCrescent(@RequestBody RedCrescent redCrescent) {
        return redCrescentService.add(redCrescent);
    }

    @DeleteMapping("/remove/{id}")
    public void removeRedCrescent(@PathVariable("id") Long id) {
        redCrescentService.remove(id);
    }

    @PutMapping("/modify/{id}")
    public RedCrescent modifyRedCrescent(@PathVariable("id") Long id, @RequestBody RedCrescent redCrescent) {
        redCrescent.setRedCID(id); // Ensure the ID from URL is set in the object
        return redCrescentService.modify(redCrescent);
    }
}
