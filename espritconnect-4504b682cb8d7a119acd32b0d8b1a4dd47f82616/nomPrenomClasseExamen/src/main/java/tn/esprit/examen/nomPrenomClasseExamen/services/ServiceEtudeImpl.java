package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ServiceEtudeRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceEtudeImpl implements IServiceEtude {

    @Autowired
    private ServiceEtudeRepository serviceEtudeRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Service_Etude addServiceEtude(Service_Etude serviceEtude) {
        return serviceEtudeRepository.save(serviceEtude);
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

    @Override
    public void assignProjetToService(Long userId, Long serviceId) {
        // Fetch the User by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Fetch the Service_Etude by ID
        Service_Etude serviceEtude = serviceEtudeRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + serviceId));

        // Add the Service_Etude to the User's list of services participated in
        user.getServiceEtudes().add(serviceEtude);

        // Add the User to the Service_Etude's list of users participating
        serviceEtude.getClients().add(user);

        // Save both entities
        userRepository.save(user);
        serviceEtudeRepository.save(serviceEtude);
    }


}
