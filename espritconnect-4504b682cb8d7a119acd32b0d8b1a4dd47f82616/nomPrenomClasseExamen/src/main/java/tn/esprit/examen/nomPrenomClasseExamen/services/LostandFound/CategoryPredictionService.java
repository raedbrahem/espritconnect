package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for predicting item categories using the enhanced AI categorizer
 */
@Service
public class CategoryPredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryPredictionService.class);
    private static final String PYTHON_SCRIPT_PATH = "ai_auto_categorizer/enhanced_categorizer.py";
    private static final String TEMP_DIR = "temp";
    
    /**
     * Represents a category prediction result
     */
    public static class CategoryPrediction {
        private String category;
        private double confidence;
        
        public CategoryPrediction(String category, double confidence) {
            this.category = category;
            this.confidence = confidence;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        @Override
        public String toString() {
            return "CategoryPrediction{" +
                    "category='" + category + '\'' +
                    ", confidence=" + confidence +
                    '}';
        }
    }
    
    /**
     * Predict the category of an item from an image file
     * 
     * @param imageFile The image file to analyze
     * @return A CategoryPrediction object containing the predicted category and confidence
     * @throws IOException If an error occurs during file processing
     */
    public CategoryPrediction predictCategory(MultipartFile imageFile) throws IOException {
        // Create temp directory if it doesn't exist
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        // Save the uploaded file temporarily
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String tempFilename = UUID.randomUUID().toString() + "." + fileExtension;
        Path tempFilePath = Paths.get(TEMP_DIR, tempFilename);
        
        try {
            Files.copy(imageFile.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved temporary file: {}", tempFilePath);
            
            // Run the Python script
            ProcessBuilder pb = new ProcessBuilder("python", PYTHON_SCRIPT_PATH, tempFilePath.toString());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            String category = "Other";
            double confidence = 0.0;
            
            while ((line = reader.readLine()) != null) {
                logger.debug("Python output: {}", line);
                
                if (line.startsWith("CATEGORY:")) {
                    category = line.substring("CATEGORY:".length()).trim();
                } else if (line.startsWith("CONFIDENCE:")) {
                    try {
                        confidence = Double.parseDouble(line.substring("CONFIDENCE:".length()).trim());
                    } catch (NumberFormatException e) {
                        logger.warn("Failed to parse confidence value: {}", line);
                    }
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.warn("Python script exited with code {}", exitCode);
            }
            
            logger.info("Category prediction: {} (confidence: {})", category, confidence);
            return new CategoryPrediction(category, confidence);
            
        } catch (Exception e) {
            logger.error("Error predicting category", e);
            return new CategoryPrediction("Other", 0.0);
        } finally {
            // Clean up the temp file
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                logger.warn("Failed to delete temp file: {}", tempFilePath);
            }
        }
    }
    
    /**
     * Get the file extension from a filename
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "jpg"; // Default extension
    }
}
