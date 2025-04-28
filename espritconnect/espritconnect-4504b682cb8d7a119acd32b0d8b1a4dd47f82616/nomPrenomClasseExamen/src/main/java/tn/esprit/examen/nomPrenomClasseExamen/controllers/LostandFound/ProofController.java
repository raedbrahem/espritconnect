package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.AiMatchingService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.EnhancedAiMatchingService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceProof;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/proof")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProofController {
    private static final Logger logger = Logger.getLogger(ProofController.class.getName());

    private final IServiceProof proofService;

    @Autowired
    private EnhancedAiMatchingService enhancedAiMatchingService;

    @Autowired
    private AiMatchingService aiMatchingService;

    @GetMapping("/all")
    public List<Proof> getAllProofs() {
        return proofService.retrieveAllProofItems();
    }

    @GetMapping("/{id}")
    public Proof getProofById(@PathVariable Long id) {
        return proofService.retrieveProofItem(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addProof(@RequestBody Proof proof) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Adding new proof: " + proof.getDescription());

            // Save the proof
            Proof savedProof = proofService.addProofItem(proof);

            // Run the matching process
            List<EnhancedAiMatchingService.MatchResult> matches = enhancedAiMatchingService.findMatches(savedProof);

            // Prepare response
            response.put("success", true);
            response.put("message", "Proof added successfully");
            response.put("proof", savedProof);
            response.put("matches", matches);
            response.put("matchCount", matches.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error adding proof: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Error adding proof: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/update")
    public Proof updateProof(@RequestBody Proof proof) {
        return proofService.modifyProofItem(proof);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProof(@PathVariable Long id) {
        proofService.removeProofItem(id);
    }

    /**
     * Find matches for a proof
     */
    @GetMapping("/matches/{id}")
    public ResponseEntity<Map<String, Object>> findMatches(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find the proof
            Proof proof = proofService.retrieveProofItem(id);
            if (proof == null) {
                response.put("success", false);
                response.put("message", "Proof not found");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Finding matches for proof: " + id);

            // Run the enhanced matching process
            List<EnhancedAiMatchingService.MatchResult> enhancedMatches = enhancedAiMatchingService.findMatches(proof);

            // Run the regular matching process
            List<AiMatchingService.MatchResult> regularMatches = aiMatchingService.findMatches(proof);

            // Prepare response
            response.put("success", true);
            response.put("message", "Matches found successfully");
            response.put("proof", proof);
            response.put("enhancedMatches", enhancedMatches);
            response.put("enhancedMatchCount", enhancedMatches.size());
            response.put("regularMatches", regularMatches);
            response.put("regularMatchCount", regularMatches.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error finding matches: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Error finding matches: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}