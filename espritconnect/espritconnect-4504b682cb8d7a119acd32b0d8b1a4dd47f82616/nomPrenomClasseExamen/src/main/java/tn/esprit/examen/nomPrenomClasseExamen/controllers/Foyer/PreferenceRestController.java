package tn.esprit.examen.nomPrenomClasseExamen.controllers.Foyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.PreferenceServiceImpl;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/preferences")
public class PreferenceRestController {

    @Autowired
    private PreferenceServiceImpl preferenceService;

    @GetMapping("/all")
    public List<Preference> getAllPreferences() {
        return preferenceService.getAllPreferences();
    }

    @GetMapping("/get/{id}")
    public Preference getPreferenceById(@PathVariable Long id) {
        return preferenceService.getPreferenceById(id);
    }

    @PostMapping("/add")
    public Preference addPreference(@RequestBody Preference preference) {
        return preferenceService.addPreference(preference);
    }

    @PutMapping("/update/{id}")
    public Preference updatePreference(@PathVariable Long id, @RequestBody Preference preference) {
        return preferenceService.updatePreference(id, preference);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePreference(@PathVariable Long id) {
        preferenceService.deletePreference(id);
    }

@GetMapping("/my-preference")
public ResponseEntity<Preference> getPreferenceByCurrentUser() {
    try {
        Preference preference = preferenceService.getPreferenceByCurrentUser();
        return ResponseEntity.ok(preference);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // renvoie 204 si aucune préférence
    }
}

}
