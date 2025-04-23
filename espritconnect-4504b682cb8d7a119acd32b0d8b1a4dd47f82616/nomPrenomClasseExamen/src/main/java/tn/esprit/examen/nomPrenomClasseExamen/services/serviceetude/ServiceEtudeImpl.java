package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ServiceEtudeRepository;
import java.util.List;

@Service
@Transactional
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

    public Service_Etude retrieveServiceEtude(Long id) {
        return serviceEtudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + id));
    }

    @Override
    @Transactional
    public void assignProjetToService(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).get();
        Service_Etude serviceEtude = serviceEtudeRepository.findById(serviceId).get();
        if (!user.getServiceEtudesProvided().contains(serviceEtude)) {
            user.getServiceEtudesProvided().add(serviceEtude);
        }
        if (!serviceEtude.getClients().contains(user)) {
            serviceEtude.getClients().add(user);
        }
        userRepository.save(user);
        serviceEtudeRepository.save(serviceEtude);
    }

    @Override
    @Transactional
    public void unassignProjetToService(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).get();
        Service_Etude serviceEtude = serviceEtudeRepository.findById(serviceId).get();
        if (user.getServiceEtudesProvided().contains(serviceEtude)) {
            user.getServiceEtudesProvided().remove(serviceEtude);
        }
        if (serviceEtude.getClients().contains(user)) {
            serviceEtude.getClients().remove(user);
        }
        userRepository.save(user);
        serviceEtudeRepository.save(serviceEtude);
    }

    @Override
    public boolean isUserAssignedToService(Long userId, Long serviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Service_Etude service = serviceEtudeRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + serviceId));

        // Check both sides of the relationship
        return user.getServiceEtudesProvided().contains(service) ||
                service.getClients().contains(user);
    }

    @Override
    public Service_Etude retrieveServiceEtudeById(Long id) {
        return serviceEtudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + id));
    }

    @Override
    public List<User> retrieveClientsByServiceEtudeId(Long serviceEtudeId) {
        Service_Etude serviceEtude = serviceEtudeRepository.findById(serviceEtudeId)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + serviceEtudeId));
        return serviceEtude.getClients();
    }
}
