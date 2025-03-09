package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServicePaiement;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/paiement")
public class PaiementRestController {

    @Autowired
    private IServicePaiement servicePaiement;

    // http://localhost:8089/tpfoyer/paiement/retrieve-all-paiements
    @GetMapping("/retrieve-all-paiements")
    public List<Paiement> getPaiements() {
        return servicePaiement.retrieveAllPaiements();
    }

    // http://localhost:8089/tpfoyer/paiement/retrieve-paiement/8
    @GetMapping("/retrieve-paiement/{paiement-id}")
    public Paiement retrievePaiement(@PathVariable("paiement-id") Long paiement_id) {
        return servicePaiement.retrievePaiement(paiement_id);
    }

    // http://localhost:8089/tpfoyer/paiement/add-paiement
    @PostMapping("/add-paiement")
    public Paiement addPaiement(@RequestBody Paiement paiement) {
        return servicePaiement.addPaiement(paiement);
    }

    // http://localhost:8089/tpfoyer/paiement/remove-paiement/{paiement-id}
    @DeleteMapping("/remove-paiement/{paiement-id}")
    public void removePaiement(@PathVariable("paiement-id") Long paiement_id) {
        servicePaiement.removePaiement(paiement_id);
    }

    // http://localhost:8089/tpfoyer/paiement/modify-paiement
    @PutMapping("/modify-paiement")
    public Paiement modifyPaiement(@RequestBody Paiement paiement) {
        return servicePaiement.modifyPaiement(paiement);
    }
}