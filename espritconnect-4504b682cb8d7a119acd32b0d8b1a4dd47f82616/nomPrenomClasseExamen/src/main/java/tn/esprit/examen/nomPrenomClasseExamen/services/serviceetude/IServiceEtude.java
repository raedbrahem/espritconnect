package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Service_Etude;

import java.util.List;

public interface IServiceEtude {
    Service_Etude addServiceEtude(Service_Etude serviceEtude);
    void deleteServiceEtude(Service_Etude serviceEtude);
    Service_Etude updateServiceEtude(Service_Etude serviceEtude);
    List<Service_Etude> getAllServiceEtudes();
    Service_Etude retrieveServiceEtude(Long id);
    void assignProjetToService(Long userId, Long serviceId);
    void unassignProjetToService(Long userId, Long serviceId);
    boolean isUserAssignedToService(Long userId, Long serviceId);
    Service_Etude retrieveServiceEtudeById(Long id);
    List<User> retrieveClientsByServiceEtudeId(Long serviceEtudeId);
    List<Service_Etude> retrieveServicesEtudeByTutorId(Long tutorId);
}
