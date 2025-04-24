package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
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
@CrossOrigin("*")
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

    /**
     * Upload a new image and run matching on it
     * @param file Image file to upload and match
     * @param description Description of the proof
     * @return Result of the matching operation
     */
    @PostMapping("/upload-and-match")
    public ResponseEntity<Map<String, Object>> uploadAndMatch(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please upload a file");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Upload to Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "uploads/proof");

            // Create a new proof
            Proof proof = new Proof();
            proof.setImage_url(imageUrl);
            proof.setDescription(description);
            proof.setDateSubmitted(LocalDateTime.now());
            proof.setValidated(false);

            // Save the proof
            Proof savedProof = proofRepository.save(proof);

            // Run the enhanced matcher
            enhancedAiMatchingService.findMatches(savedProof);

            // Get raw matches for debugging
            List<String> matches = enhancedAiMatchingService.runEnhancedHybridMatcher(imageUrl);

            response.put("success", true);
            response.put("message", "Image uploaded and matching process completed");
            response.put("proofId", savedProof.getId_proof());
            response.put("imageUrl", imageUrl);
            response.put("matches", matches);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error in matching process: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Run direct matching on a URL without creating a proof
     * @param imageUrl URL of the image to match
     * @return Raw matching results
     */
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
