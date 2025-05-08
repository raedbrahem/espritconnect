package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.CategoryPredictionService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.CategoryPredictionService.CategoryPrediction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for item category prediction
 */
@RestController
@RequestMapping("/item")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryPredictionController {
    
    @Autowired
    private CategoryPredictionService categoryPredictionService;
    
    /**
     * Predict the category of an item from an image
     * 
     * @param image The image file to analyze
     * @return A JSON response with the predicted category and confidence
     */
    @PostMapping("/predict-category")
    public ResponseEntity<?> predictCategory(@RequestParam("image") MultipartFile image) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload an image file");
            }
            
            // Check if the file is an image
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File must be an image");
            }
            
            // Predict the category
            CategoryPrediction prediction = categoryPredictionService.predictCategory(image);
            
            // Create the response
            Map<String, Object> response = new HashMap<>();
            response.put("category", prediction.getCategory());
            response.put("confidence", prediction.getConfidence());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to process image: " + e.getMessage());
        }
    }
}
