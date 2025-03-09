package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;

import java.util.List;

public interface IServicePaiement {
    List<Paiement> retrieveAllPaiements();
    Paiement retrievePaiement(Long id_paiement); // Utilisation de Long au lieu de long
    Paiement addPaiement(Paiement paiement);
    void removePaiement(Long id_paiement); // Utilisation de Long au lieu de long
    Paiement modifyPaiement(Paiement paiement);
}
