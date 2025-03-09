package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServiceTrajet;

import java.util.List;

    @RestController
    @AllArgsConstructor
    @RequestMapping("/trajet")
    public class TrajetRestController {

        @Autowired
        private IServiceTrajet serviceTrajet;

        // http://localhost:8089/tpfoyer/trajet/retrieve-all-trajets
        @GetMapping("/retrieve-all-trajets")
        public List<Trajet> getTrajets() {
            return serviceTrajet.retrieveAllTrajets();
        }

        // http://localhost:8089/tpfoyer/trajet/retrieve-trajet/8
        @GetMapping("/retrieve-trajet/{trajet-id}")
        public Trajet retrieveTrajet(@PathVariable("trajet-id") Long trajet_id) {
            return serviceTrajet.retrieveTrajet(trajet_id);
        }

        // http://localhost:8089/tpfoyer/trajet/add-trajet
        @PostMapping("/add-trajet")
        public Trajet addTrajet(@RequestBody Trajet trajet) {
            return serviceTrajet.addTrajet(trajet);
        }

        // http://localhost:8089/tpfoyer/bloc/remove-bloc/{bloc-id}
        @DeleteMapping("/remove-trajet/{trajet-id}")
        public void removeTrajet(@PathVariable("trajet-id") Long trajet_id) {
            serviceTrajet.removeTrajet(trajet_id);
        }

        // http://localhost:8089/tpfoyer/bloc/modify-bloc
        @PutMapping("/modify-trajet")
        public Trajet modifyTrajet(@RequestBody Trajet trajet) {
            return serviceTrajet.modifyTrajet(trajet);
        }
    }


