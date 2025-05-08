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
    private static final String PYTHON_SCRIPT_PATH = "C:/Users/Tifa/Desktop/Master pull Spring/espritconnect/espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616/nomPrenomClasseExamen/ai_auto_categorizer/run_categorizer.bat";
    private static final String TEMP_DIR_PATH = "C:/Users/Tifa/Desktop/Master pull Spring/espritconnect/espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616/nomPrenomClasseExamen/temp";

    private final String pythonScriptPath = PYTHON_SCRIPT_PATH;
    private final String tempDirPath;

    public CategoryPredictionService() {
        // Use the hardcoded paths directly
        tempDirPath = TEMP_DIR_PATH;

        logger.info("Using Python script path: {}", pythonScriptPath);
        logger.info("Using temp directory path: {}", tempDirPath);

        // Verify the Python script exists
        File scriptFile = new File(pythonScriptPath);
        if (!scriptFile.exists()) {
            logger.error("Python script not found at: {}", pythonScriptPath);
        } else {
            logger.info("Python script found at: {}", pythonScriptPath);
        }

        // Create temp directory if it doesn't exist
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            boolean created = tempDir.mkdirs();
            if (created) {
                logger.info("Created temp directory: {}", tempDirPath);
            } else {
                logger.error("Failed to create temp directory: {}", tempDirPath);
            }
        } else {
            logger.info("Temp directory already exists: {}", tempDirPath);
        }


    }



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
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // Save the uploaded file temporarily
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String tempFilename = UUID.randomUUID().toString() + "." + fileExtension;
        Path tempFilePath = Paths.get(tempDirPath, tempFilename);

        try {
            Files.copy(imageFile.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved temporary file: {}", tempFilePath);

            // Run the Python script with full path
            File scriptFile = new File(pythonScriptPath);
            if (!scriptFile.exists()) {
                logger.error("Python script not found at: {}", pythonScriptPath);
                return new CategoryPrediction("Other", 0.0);
            }

            logger.info("Running Python script: {}", pythonScriptPath);
            // Use ProcessBuilder with the batch file
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(pythonScriptPath, tempFilePath.toString());

            // Log the command for debugging
            logger.info("Command: \"{}\" \"{}\"", pythonScriptPath, tempFilePath.toString());
            pb.redirectErrorStream(true);

            // Set the working directory to the script's directory
            pb.directory(scriptFile.getParentFile());

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            String category = "Other";
            double confidence = 0.0;

            StringBuilder outputBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // Log all output at INFO level for better visibility
                logger.info("Python output: {}", line);
                outputBuilder.append(line).append("\n");

                if (line.startsWith("CATEGORY:")) {
                    category = line.substring("CATEGORY:".length()).trim();
                    logger.info("Detected category: {}", category);
                } else if (line.startsWith("CONFIDENCE:")) {
                    try {
                        confidence = Double.parseDouble(line.substring("CONFIDENCE:".length()).trim());
                        logger.info("Detected confidence: {}", confidence);
                    } catch (NumberFormatException e) {
                        logger.warn("Failed to parse confidence value: {}", line);
                    }
                }
            }

            // Log the complete output for debugging
            String completeOutput = outputBuilder.toString();
            if (completeOutput.isEmpty()) {
                logger.warn("No output received from Python script");
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
                logger.info("Deleted temporary file: {}", tempFilePath);
            } catch (IOException e) {
                logger.warn("Failed to delete temp file: {}", tempFilePath);
            }
        }
    }

    /**
     * Predict the category of an item from a Cloudinary URL
     *
     * @param imageUrl The Cloudinary URL of the image to analyze
     * @return A CategoryPrediction object containing the predicted category and confidence
     */
    public CategoryPrediction predictCategoryFromUrl(String imageUrl) {
        logger.info("Predicting category from URL: {}", imageUrl);

        try {
            // Download the image from the URL
            java.net.URL url = new java.net.URL(imageUrl);
            String tempFilename = UUID.randomUUID().toString() + ".jpg";
            Path tempFilePath = Paths.get(tempDirPath, tempFilename);

            // Create temp directory if it doesn't exist
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // Download the image
            logger.info("Downloading image from URL to: {}", tempFilePath);
            try (java.io.InputStream in = url.openStream()) {
                Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Run the Python script with full path
            File scriptFile = new File(pythonScriptPath);
            if (!scriptFile.exists()) {
                logger.error("Python script not found at: {}", pythonScriptPath);
                return new CategoryPrediction("Other", 0.0);
            }

            logger.info("Running Python script: {}", pythonScriptPath);
            // Use ProcessBuilder with the batch file
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(pythonScriptPath, tempFilePath.toString());

            // Log the command for debugging
            logger.info("Command: \"{}\" \"{}\"", pythonScriptPath, tempFilePath.toString());
            pb.redirectErrorStream(true);

            // Set the working directory to the script's directory
            pb.directory(scriptFile.getParentFile());

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            String category = "Other";
            double confidence = 0.0;

            StringBuilder outputBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // Log all output at INFO level for better visibility
                logger.info("Python output: {}", line);
                outputBuilder.append(line).append("\n");

                if (line.startsWith("CATEGORY:")) {
                    category = line.substring("CATEGORY:".length()).trim();
                    logger.info("Detected category: {}", category);
                } else if (line.startsWith("CONFIDENCE:")) {
                    try {
                        confidence = Double.parseDouble(line.substring("CONFIDENCE:".length()).trim());
                        logger.info("Detected confidence: {}", confidence);
                    } catch (NumberFormatException e) {
                        logger.warn("Failed to parse confidence value: {}", line);
                    }
                }
            }

            // Log the complete output for debugging
            String completeOutput = outputBuilder.toString();
            if (completeOutput.isEmpty()) {
                logger.warn("No output received from Python script");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.warn("Python script exited with code {}", exitCode);
            }

            logger.info("Category prediction: {} (confidence: {})", category, confidence);

            // Clean up the temp file
            try {
                Files.deleteIfExists(tempFilePath);
                logger.info("Deleted temporary file: {}", tempFilePath);
            } catch (IOException e) {
                logger.warn("Failed to delete temp file: {}", tempFilePath);
            }

            return new CategoryPrediction(category, confidence);

        } catch (Exception e) {
            logger.error("Error predicting category from URL", e);
            return new CategoryPrediction("Other", 0.0);
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
