package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Service_Etude;

import java.util.List;

public interface IServiceEtude {
    void addServiceEtude(Service_Etude serviceEtude);
    void deleteServiceEtude(Service_Etude serviceEtude);
    Service_Etude updateServiceEtude(Service_Etude serviceEtude);
    List<Service_Etude> getAllServiceEtudes();
    Service_Etude retrieveServiceEtude(Long id);
}
