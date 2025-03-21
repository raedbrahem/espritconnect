package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ServiceEtudeRepository;
import java.util.List;

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

    public Service_Etude retrieveServiceEtude(Long id) {
        return serviceEtudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service_Etude not found with ID: " + id));
    }

    @Override
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
}
