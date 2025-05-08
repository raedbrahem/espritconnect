package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.AiMatchingService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.CloudinaryServicee;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.EnhancedAiMatchingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for AI matching operations
 */
@RestController
@RequestMapping("/api/ai-matching")
@CrossOrigin(origins = "http://localhost:4200")
public class AiMatchingController {

    @Autowired
    private EnhancedAiMatchingService enhancedAiMatchingService;

    @Autowired
    private CloudinaryServicee cloudinaryService;

    @Autowired
    private ProofRepository proofRepository;

    /**
     * Test endpoint to run the enhanced matcher on a specific proof
     * @param proofId ID of the proof to match
     * @return Result of the matching operation
     */
    @GetMapping("/test/{proofId}")
    public ResponseEntity<Map<String, Object>> testMatchingOnProof(@PathVariable Long proofId) {
        Map<String, Object> response = new HashMap<>();

        Proof proof = proofRepository.findById(proofId).orElse(null);
        if (proof == null) {
            response.put("success", false);
            response.put("message", "Proof not found with ID: " + proofId);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Run the enhanced matcher
            enhancedAiMatchingService.findMatches(proof);

            response.put("success", true);
            response.put("message", "Matching process completed successfully");
            response.put("proofId", proofId);
            response.put("imageUrl", proof.getImage_url());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error running matching: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @GetMapping("/direct-match")
    public ResponseEntity<Map<String, Object>> directMatch(@RequestParam String imageUrl) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> matches = enhancedAiMatchingService.runEnhancedHybridMatcher(imageUrl);

            response.put("success", true);
            response.put("matches", matches);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error in matching process: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
