package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.TypeTrajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.TrajetRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.IServiceTrajet;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/trajet")
public class TrajetRestController {

    @Autowired
    private IServiceTrajet serviceTrajet;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrajetRepository trajetRepository;
    private IServiceTrajet trajetService;

    @GetMapping("/get-all-trajets")
    public ResponseEntity<List<Trajet>> getAllTrajets() {
        // Récupérer tous les trajets depuis le repository
        List<Trajet> trajets = trajetRepository.findAll();

        // Retourner la liste des trajets avec un statut HTTP 200 OK
        return ResponseEntity.ok(trajets);
    }

    @Transactional
    // http://localhost:8089/tpfoyer/trajet/retrieve-trajet/8
    @GetMapping("/retrieve-trajet/{trajet-id}")
    public Trajet retrieveTrajet(@PathVariable("trajet-id") Long trajet_id) {
        return serviceTrajet.retrieveTrajet(trajet_id);
    }

    @Transactional
    // http://localhost:8089/tpfoyer/trajet/add-trajet
    @PostMapping("/add-trajet")
    public Trajet addTrajet(@RequestBody Trajet trajet) {
        try {
            // Récupérer l'utilisateur authentifié à partir du contexte de sécurité
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername(); // L'email est utilisé comme identifiant unique

            // Récupérer l'utilisateur associé à ce trajet à partir de la base de données
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Associer l'utilisateur au trajet
            trajet.setConducteur(user); // Associer l'utilisateur comme conducteur du trajet
            trajet.setCreatedAt(new Date());
            trajet.setTypeTrajet(TypeTrajet.EN_ATTENTE); // <-- Ajout crucial
            trajet.setEstDisponible(false);// Définir la date de création du trajet

            // Sauvegarder le trajet dans la base de données
            return trajetRepository.save(trajet); // Utiliser l'instance de trajetRepository injectée pour enregistrer le trajet
        } catch (Exception e) {
            // Gérer les erreurs (par exemple, utilisateur introuvable ou autres)
            throw new RuntimeException("Erreur lors de l'ajout du trajet: " + e.getMessage(), e);
        }
    }

    @Transactional
    // http://localhost:8089/tpfoyer/bloc/remove-bloc/{bloc-id}
    @DeleteMapping("/remove-trajet/{trajet-id}")
    public void removeTrajet(@PathVariable("trajet-id") Long trajet_id) {
        serviceTrajet.removeTrajet(trajet_id);
    }

    @Transactional
    @PutMapping("/modify-trajet/{id}")
    public ResponseEntity<?> modifyTrajet(@PathVariable Long id, @RequestBody Trajet trajetModifie) {
        try {
            Optional<Trajet> existingTrajetOpt = trajetRepository.findById(id);

            if (existingTrajetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Trajet non trouvé avec l'ID : " + id);
            }

            Trajet existingTrajet = existingTrajetOpt.get();

            // Vérifier que l'utilisateur est bien le propriétaire
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));

            if (!existingTrajet.getConducteur().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous n'êtes pas autorisé à modifier ce trajet.");
            }

            // Mettre à jour SEULEMENT les champs modifiables
            existingTrajet.setPoint_depart(trajetModifie.getPoint_depart());
            existingTrajet.setPoint_arrivee(trajetModifie.getPoint_arrivee());
            existingTrajet.setPlacesDisponibles(trajetModifie.getPlacesDisponibles());
            existingTrajet.setMontant(trajetModifie.getMontant());
            // PAS de modification sur createdAt, conducteur, estDisponible ici !

            // Sauvegarder
            trajetRepository.save(existingTrajet);

            return ResponseEntity.ok(existingTrajet);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la modification du trajet : " + e.getMessage());
        }
    }



    @GetMapping("/detail/{id_trajet}")
    public ResponseEntity<Trajet> getTrajetDetail(@PathVariable Long id_trajet) {

        // Récupérer le trajet par son ID
        Trajet trajet = trajetService.retrieveTrajet(id_trajet);
        return ResponseEntity.ok(trajet);


    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/confirmer-trajet/{id}")
    public ResponseEntity<Trajet> confirmerTrajet(@PathVariable Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        trajet.confirmerTrajet();
        trajetRepository.save(trajet);

        return ResponseEntity.ok(trajet);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/annuler-trajet/{id}")
    public ResponseEntity<Trajet> annulerTrajet(@PathVariable Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        trajet.annulerTrajet();
        trajetRepository.save(trajet);

        return ResponseEntity.ok(trajet);
    }

    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Long>> getTrajetStatistics() {
        Map<String, Long> stats = new LinkedHashMap<>();

        // Initialiser tous les statuts à 0
        for (TypeTrajet type : TypeTrajet.values()) {
            stats.put(type.name(), 0L);
        }

        // Récupérer les stats réelles depuis la base
        List<Object[]> results = trajetRepository.countTrajetsByType();

        // Mettre à jour les valeurs existantes
        for (Object[] result : results) {
            TypeTrajet type = (TypeTrajet) result[0];
            Long count = (Long) result[1];
            stats.put(type.name(), count);
        }

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/my-trajets")
    public ResponseEntity<List<Trajet>> getMyTrajets() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Trajet> trajets = serviceTrajet.getTrajetsByUserId(user.getId());
        return ResponseEntity.ok(trajets);
    }


    @GetMapping("/statistiquess")
    public ResponseEntity<Map<String, Long>> getUserTrajetStatistics() {
        // Récupérer l'utilisateur connecté
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Long> stats = new LinkedHashMap<>();

        // Initialiser tous les statuts à 0
        for (TypeTrajet type : TypeTrajet.values()) {
            stats.put(type.name(), 0L);
        }

        // Récupérer les stats réelles depuis la base pour cet utilisateur
        List<Object[]> results = trajetRepository.countTrajetsByUserAndType(user.getId());

        // Mettre à jour les valeurs existantes
        for (Object[] result : results) {
            TypeTrajet type = (TypeTrajet) result[0];
            Long count = (Long) result[1];
            stats.put(type.name(), count);
        }

        return ResponseEntity.ok(stats);
    }


}





