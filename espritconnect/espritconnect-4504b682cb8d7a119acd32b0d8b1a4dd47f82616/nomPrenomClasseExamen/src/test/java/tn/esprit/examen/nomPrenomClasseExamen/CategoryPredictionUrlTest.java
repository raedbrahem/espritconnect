package tn.esprit.examen.nomPrenomClasseExamen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.CategoryPredictionService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CategoryPredictionUrlTest {

    @Autowired
    private CategoryPredictionService categoryPredictionService;

    @Test
    public void testCategoryPredictionFromUrl() {
        // Use a sample Cloudinary URL for testing
        // Replace this with a valid Cloudinary URL from your account
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg";
        
        // Test the category prediction from URL
        CategoryPredictionService.CategoryPrediction prediction = 
                categoryPredictionService.predictCategoryFromUrl(imageUrl);
        
        // Verify the prediction
        assertNotNull(prediction, "Prediction should not be null");
        System.out.println("Predicted category from URL: " + prediction.getCategory());
        System.out.println("Confidence: " + prediction.getConfidence());
    }
}
