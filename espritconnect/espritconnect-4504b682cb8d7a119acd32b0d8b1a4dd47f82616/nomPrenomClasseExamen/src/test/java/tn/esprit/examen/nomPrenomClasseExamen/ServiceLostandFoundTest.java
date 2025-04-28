package tn.esprit.examen.nomPrenomClasseExamen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.ServiceLostandFoundImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ServiceLostandFoundTest {

    @Autowired
    private ServiceLostandFoundImpl serviceLostandFound;

    @Test
    public void testAddLostItemWithCloudinaryUrl() {
        // Create a test item with a Cloudinary URL
        Item item = new Item();
        item.setItem_name("Test Item");
        item.setDescription("Test Description");
        item.setDatePerdu(LocalDate.now());
        item.setLieuPerdu("Test Location");
        item.setRetrouve(false);
        
        // Use a sample Cloudinary URL - replace with a valid URL from your account
        item.setItem_image("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
        
        // Add the item
        Item savedItem = serviceLostandFound.addLostItem(item);
        
        // Verify the item was saved
        assertNotNull(savedItem, "Saved item should not be null");
        assertNotNull(savedItem.getId_item(), "Saved item should have an ID");
        
        System.out.println("Test completed successfully!");
        System.out.println("Item ID: " + savedItem.getId_item());
        System.out.println("Item Category: " + savedItem.getCategory());
    }
    
    @Test
    public void testUploadItemOrProof() throws IOException {
        // Create a test image
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
        
        // Call the uploadItemOrProof method
        serviceLostandFound.uploadItemOrProof(
                "Test Item",
                "Test Description",
                LocalDate.now(),
                "Test Location",
                false, // not found (lost item)
                multipartFile
        );
        
        // Clean up
        input.close();
        
        System.out.println("Upload test completed!");
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
