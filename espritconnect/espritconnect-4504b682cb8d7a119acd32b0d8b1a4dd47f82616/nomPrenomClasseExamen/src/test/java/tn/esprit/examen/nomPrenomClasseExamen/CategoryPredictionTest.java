package tn.esprit.examen.nomPrenomClasseExamen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.CategoryPredictionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class CategoryPredictionTest {

    @Autowired
    private CategoryPredictionService categoryPredictionService;

    @Test
    public void testCategoryPrediction() throws IOException {
        // Create a test image if needed
        Path testImagePath = createTestImage();
        
        // Load the test image as a MultipartFile
        File imageFile = testImagePath.toFile();
        FileInputStream input = new FileInputStream(imageFile);
        MultipartFile multipartFile = new MockMultipartFile(
                "test_image.jpg",
                "test_image.jpg",
                "image/jpeg",
                input
        );
        
        // Test the category prediction
        CategoryPredictionService.CategoryPrediction prediction = 
                categoryPredictionService.predictCategory(multipartFile);
        
        // Verify the prediction
        assertNotNull(prediction, "Prediction should not be null");
        System.out.println("Predicted category: " + prediction.getCategory());
        System.out.println("Confidence: " + prediction.getConfidence());
        
        // Clean up
        input.close();
    }
    
    private Path createTestImage() throws IOException {
        // Create a simple test image (red rectangle on white background)
        Path tempDir = Paths.get("temp");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        
        Path testImagePath = Paths.get("temp", "test_image.jpg");
        
        // Check if the test image already exists
        if (!Files.exists(testImagePath)) {
            // Copy a sample image from the resources folder if available
            Path sampleImagePath = Paths.get("src", "test", "resources", "sample_image.jpg");
            if (Files.exists(sampleImagePath)) {
                Files.copy(sampleImagePath, testImagePath);
            } else {
                // If no sample image is available, create a new one using Java2D
                // This is a simplified version - in a real test, you might want to use a real image
                byte[] imageData = new byte[1024]; // Dummy image data
                Files.write(testImagePath, imageData);
            }
        }
        
        return testImagePath;
    }
}
