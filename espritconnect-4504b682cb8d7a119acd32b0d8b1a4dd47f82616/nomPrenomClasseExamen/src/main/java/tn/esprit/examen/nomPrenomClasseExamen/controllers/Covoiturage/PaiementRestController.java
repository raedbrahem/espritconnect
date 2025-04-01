package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.StatutPaiement;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServicePaiement;


@RestController
@RequestMapping("/api/paiement")
public class PaiementRestController {

    @Autowired
    private IServicePaiement servicePaiement;

    // Route pour effectuer un paiement
    @PostMapping("/effectuer/{reservationId}")
    public Paiement effectuerPaiement(@PathVariable Long reservationId, @RequestParam double montant, @RequestParam String moyenPaiement) {
        return servicePaiement.effectuerPaiement(reservationId, montant, moyenPaiement);
    }

    // Route pour mettre à jour le statut du paiement (par exemple, après un retour de service de paiement comme Stripe)
    @PostMapping("/mettre-a-jour/{paiementId}")
    public Paiement mettreAJourStatutPaiement(@PathVariable Long paiementId, @RequestParam String statut) {
        return servicePaiement.mettreAJourStatutPaiement(paiementId, StatutPaiement.valueOf(statut));
    }
}
