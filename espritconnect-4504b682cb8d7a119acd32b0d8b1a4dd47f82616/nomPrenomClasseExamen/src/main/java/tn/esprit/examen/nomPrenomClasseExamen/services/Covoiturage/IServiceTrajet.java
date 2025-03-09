package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;

import java.util.List;

public interface IServiceTrajet {
    List<Trajet> retrieveAllTrajets();
    Trajet retrieveTrajet(Long id_trajet); // Utilisation de Long au lieu de long
    Trajet addTrajet(Trajet trajet);
    void removeTrajet(Long id_trajet); // Utilisation de Long au lieu de long
    Trajet modifyTrajet(Trajet trajet);
}
