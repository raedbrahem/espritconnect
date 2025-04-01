package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.StatutPaiement;

import java.util.List;

public interface IServicePaiement {
    Paiement effectuerPaiement(Long reservationId, double montant, String moyenPaiement);
    Paiement mettreAJourStatutPaiement(Long paiementId, StatutPaiement statut);
}
