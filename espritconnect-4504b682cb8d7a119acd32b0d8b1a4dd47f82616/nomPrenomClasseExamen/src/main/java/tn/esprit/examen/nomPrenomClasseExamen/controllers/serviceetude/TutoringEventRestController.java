package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IServiceTutoringEvent;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/tutoring-event")
@RestController
public class TutoringEventRestController {
    @Autowired
    private IServiceTutoringEvent tutoringEventService;

    @PostMapping("/add")
    public TutoringEvent addTutoringEvent(@RequestBody TutoringEvent tutoringEvent) {
        return tutoringEventService.addTutoringEvent(tutoringEvent);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTutoringEvent(@PathVariable Long id) {
        TutoringEvent tutoringEvent = new TutoringEvent();
        tutoringEvent.setId(id);
        tutoringEventService.deleteTutoringEvent(tutoringEvent);
    }

    @PutMapping("/update/{id}")
    public TutoringEvent updateTutoringEvent(
            @PathVariable("id") Long id, // Get the ID from the URL
            @RequestBody TutoringEvent tutoringEvent) { // Get the updated data from the request body
        tutoringEvent.setId(id); // Set the ID from the URL to the request body
        return tutoringEventService.updateTutoringEvent(tutoringEvent); // Use the service to update the entity
    }

    @GetMapping("/all")
    public List<TutoringEvent> getAllTutoringEvents() {
        return tutoringEventService.getAllTutoringEvents();
    }
}