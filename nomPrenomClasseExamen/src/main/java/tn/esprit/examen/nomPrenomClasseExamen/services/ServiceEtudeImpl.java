package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ServiceEtudeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceEtudeImpl implements IServiceEtude {

    @Autowired
    private ServiceEtudeRepository serviceEtudeRepository;

    @Override
    public void addServiceEtude(Service_Etude serviceEtude) {
        serviceEtudeRepository.save(serviceEtude);
    }

    @Override
    public void deleteServiceEtude(Service_Etude serviceEtude) {
        serviceEtudeRepository.delete(serviceEtude);
    }

    @Override
    public Service_Etude updateServiceEtude(Service_Etude serviceEtude) {
            return serviceEtudeRepository.save(serviceEtude);
    }

    @Override
    public List<Service_Etude> getAllServiceEtudes() {
        return serviceEtudeRepository.findAll();
    }

    // Additional method to retrieve a Service_Etude by ID
    public Service_Etude retrieveServiceEtude(Long id) {
        return serviceEtudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + id));
    }




    // Additional method to assign a Service_Etude to a user (if needed)
    public void assignServiceEtudeToUser(Long userId, Long serviceEtudeId) {
        // Assuming you have a UserRepository and User entity
        // User user = userRepository.findById(userId)
        //         .orElseThrow(() -> new RuntimeException("User not found"));
        // Service_Etude serviceEtude = serviceEtudeRepository.findById(serviceEtudeId)
        //         .orElseThrow(() -> new RuntimeException("Service_Etude not found"));

        // Set the relation (e.g., serviceEtude.setUser(user))
        // serviceEtudeRepository.save(serviceEtude);
    }
}