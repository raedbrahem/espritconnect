package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.TutoringEvent;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IServiceEtude;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IServiceTutoringEvent;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/tutoring-event")
@RestController
public class TutoringEventRestController {

    @Autowired
    private final IServiceTutoringEvent tutoringEventService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final IServiceEtude serviceEtudeService;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/retrieve/{id}")
    public TutoringEvent retrieveTutoringEvent(@PathVariable Long id) {
        TutoringEvent tutoringEvent = tutoringEventService.retrieveTutoringEvent(id);
        // Ensure the logged-in user is the tutor of the associated Service_Etude
        Service_Etude serviceEtude = tutoringEvent.getServiceEtude();
        User user = getAuthenticatedUser();
        if (serviceEtude.getTutor() == null || !serviceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view this event.");
        }
        return tutoringEvent;
    }

    @PostMapping("/add")
    public TutoringEvent addTutoringEvent(@RequestBody TutoringEvent tutoringEvent) {
        User user = getAuthenticatedUser();

        // Get only the ID from the request
        Long serviceEtudeId = tutoringEvent.getServiceEtude().getId();

        // Fetch the full Service_Etude from the database, including the tutor
        Service_Etude fullServiceEtude = serviceEtudeService.retrieveServiceEtudeById(serviceEtudeId);

        if (fullServiceEtude.getTutor() == null || !fullServiceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to add events for this service.");
        }

        // Replace the minimal serviceEtude with the full one in the event
        tutoringEvent.setServiceEtude(fullServiceEtude);

        return tutoringEventService.addTutoringEvent(tutoringEvent);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTutoringEvent(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        TutoringEvent tutoringEvent = tutoringEventService.retrieveTutoringEvent(id);

        // Check if the logged-in user is the tutor of the associated Service_Etude
        if (!tutoringEvent.getServiceEtude().getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this event.");
        }

        tutoringEventService.deleteTutoringEvent(tutoringEvent);
    }

    @PutMapping("/update/{id}")
    public TutoringEvent updateTutoringEvent(@PathVariable("id") Long id,
                                             @RequestBody TutoringEvent tutoringEvent) {
        User user = getAuthenticatedUser();
        TutoringEvent existing = tutoringEventService.retrieveTutoringEvent(id);

        // Check if the logged-in user is the tutor of the associated Service_Etude
        if (!existing.getServiceEtude().getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this event.");
        }

        tutoringEvent.setId(id);
        tutoringEvent.setServiceEtude(existing.getServiceEtude()); // make sure we keep the same service
        return tutoringEventService.updateTutoringEvent(tutoringEvent);
    }

    @GetMapping("/all")
    public List<TutoringEvent> getAllTutoringEvents() {
        return tutoringEventService.getAllTutoringEvents();
    }
}

