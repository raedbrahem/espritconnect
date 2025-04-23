package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IServiceEtude;
import  org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/service-etude")
@RestController
public class ServiceEtudeRestController {

    @Autowired
    private IServiceEtude serviceEtudeService;

    private final UserService userService;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/add")
    public Service_Etude addServiceEtude(@RequestBody Service_Etude serviceEtude) {
        User user = getAuthenticatedUser();
        serviceEtude.setTutor(user);
        return serviceEtudeService.addServiceEtude(serviceEtude);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteServiceEtude(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        Service_Etude serviceEtude = serviceEtudeService.retrieveServiceEtude(id);

        // Check if the authenticated user is the tutor for this service
        if (serviceEtude.getTutor() == null || !serviceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this service.");
        }
        serviceEtudeService.deleteServiceEtude(serviceEtude);
    }

    @PutMapping("/update/{id}")
    public Service_Etude updateServiceEtude(@PathVariable Long id, @RequestBody Service_Etude serviceEtude) {
        User user = getAuthenticatedUser();
        serviceEtude.setId(id);
        serviceEtude.setTutor(user);

        // Check if the authenticated user is the tutor for this service
        if (serviceEtude.getTutor() == null || !serviceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this service.");
        }
        return serviceEtudeService.updateServiceEtude(serviceEtude);
    }

    @GetMapping("/all")
    public List<Service_Etude> getAllServiceEtudes() {
        return serviceEtudeService.getAllServiceEtudes();
    }

    @GetMapping("/retrieve/{id}")
    public Service_Etude retrieveServiceEtude(@PathVariable Long id) {
        return serviceEtudeService.retrieveServiceEtude(id);
    }

    @PostMapping("/assign/{userId}/{serviceId}")
    public void assignProjetToService(@PathVariable Long userId, @PathVariable Long serviceId) {
        User user = getAuthenticatedUser();
        Service_Etude serviceEtude = serviceEtudeService.retrieveServiceEtude(serviceId);

        // Check if the authenticated user is the tutor for this service
        if (serviceEtude.getTutor() == null || !serviceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to assign to this service.");
        }
        serviceEtudeService.assignProjetToService(userId, serviceId);
    }

    @PostMapping("/unassign/{userId}/{serviceId}")
    public void unassignProjetToService(@PathVariable Long userId, @PathVariable Long serviceId) {
        User user = getAuthenticatedUser();
        Service_Etude serviceEtude = serviceEtudeService.retrieveServiceEtude(serviceId);

        // Check if the authenticated user is the tutor for this service
        if (serviceEtude.getTutor() == null || !serviceEtude.getTutor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to unassign from this service.");
        }
        serviceEtudeService.unassignProjetToService(userId, serviceId);
    }

    @GetMapping("/is-assigned/{userId}/{serviceId}")
    public boolean isUserAssignedToService(@PathVariable Long userId, @PathVariable Long serviceId) {
        return serviceEtudeService.isUserAssignedToService(userId, serviceId);
    }

    @GetMapping("/clients/{serviceEtudeId}")
    public List<User> getClientsByServiceEtudeId(@PathVariable Long serviceEtudeId) {
        return serviceEtudeService.retrieveClientsByServiceEtudeId(serviceEtudeId);
    }
}

