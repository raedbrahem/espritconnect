package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.IServiceEtude;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular frontend
@RequiredArgsConstructor
@RequestMapping("/service-etude")
@RestController
public class ServiceEtudeRestController {

    @Autowired
    private IServiceEtude serviceEtudeService;

    @PostMapping("/add")
    public Service_Etude addServiceEtude(@RequestBody Service_Etude serviceEtude) {
        return  serviceEtudeService.addServiceEtude(serviceEtude);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteServiceEtude(@PathVariable Long id) {
        Service_Etude serviceEtude = new Service_Etude();
        serviceEtude.setId(id);
        serviceEtudeService.deleteServiceEtude(serviceEtude);
    }

    @PutMapping("/update/{id}")
    public Service_Etude updateServiceEtude(
            @PathVariable("id") Long id, // Get the ID from the URL
            @RequestBody Service_Etude serviceEtude) { // Get the updated data from the request body
        serviceEtude.setId(id); // Set the ID from the URL to the request body
        return serviceEtudeService.updateServiceEtude(serviceEtude); // Use the service to update the entity
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
        serviceEtudeService.assignProjetToService(userId, serviceId);
    }

    @PostMapping("/unassign/{userId}/{serviceId}")
    public void unassignProjetToService(@PathVariable Long userId, @PathVariable Long serviceId) {
        serviceEtudeService.unassignProjetToService(userId, serviceId);
    }
}