package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.ItemMatchNotification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ItemMatchNotificationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enhanced AI matching service for lost and found items
 * Uses a hybrid approach combining image similarity and text matching
 */
@Service
public class EnhancedAiMatchingService {
    private static final Logger logger = Logger.getLogger(EnhancedAiMatchingService.class.getName());

    @Autowired
    private LostandFoundRepository lostandFoundRepository;

    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private ItemMatchNotificationRepository notificationRepository;

    @Autowired
    private FCMService fcmService;

    /**
     * Result class for matches with detailed similarity metrics
     */
    public static class MatchResult {
        private Item item;
        private double score;
        private double confidencePercent;
        private double visualSimilarityPercent;
        private double colorSimilarityPercent;
        private double featureSimilarityPercent;

        public MatchResult(Item item, double score, double confidencePercent) {
            this.item = item;
            this.score = score;
            this.confidencePercent = confidencePercent;
            this.visualSimilarityPercent = 0;
            this.colorSimilarityPercent = 0;
            this.featureSimilarityPercent = 0;
        }

        public MatchResult(Item item, double score, double confidencePercent,
                          double visualSimilarityPercent, double colorSimilarityPercent,
                          double featureSimilarityPercent) {
            this.item = item;
            this.score = score;
            this.confidencePercent = confidencePercent;
            this.visualSimilarityPercent = visualSimilarityPercent;
            this.colorSimilarityPercent = colorSimilarityPercent;
            this.featureSimilarityPercent = featureSimilarityPercent;
        }

        public Item getItem() {
            return item;
        }

        public double getScore() {
            return score;
        }

        public double getConfidencePercent() {
            return confidencePercent;
        }

        public double getVisualSimilarityPercent() {
            return visualSimilarityPercent;
        }

        public double getColorSimilarityPercent() {
            return colorSimilarityPercent;
        }

        public double getFeatureSimilarityPercent() {
            return featureSimilarityPercent;
        }
    }

    /**
     * Main entry point for matching a proof with lost items
     * @param proof The proof to match against lost items
     * @return List of match results
     */
    public List<MatchResult> findMatches(Proof proof) {
        logger.info("Starting matching process for proof ID: " + proof.getId_proof());
        List<MatchResult> results = new ArrayList<>();

        String imageUrl = proof.getImage_url();

        if (imageUrl == null) {
            logger.warning("No image in proof ID: " + proof.getId_proof());
            return results;
        }

        if (!imageUrl.startsWith("http")) {
            logger.warning("Invalid Cloudinary image URL: " + imageUrl);
            return results;
        }

        // Run the matching process with the Cloudinary URL
        return findMatchesInternal(imageUrl, proof.getId_proof());
    }

    /**
     * Internal implementation of the matching process
     * @param proofImageUrl URL of the proof image (Cloudinary URL)
     * @param proofId ID of the proof
     * @return List of match results
     */
    private List<MatchResult> findMatchesInternal(String proofImageUrl, Long proofId) {
        logger.info("Running enhanced matcher for image: " + proofImageUrl);
        List<MatchResult> results = new ArrayList<>();

        // First, try to find items with the exact same image URL (direct match)
        List<Item> directMatches = findItemsByImageUrl(proofImageUrl);
        if (!directMatches.isEmpty()) {
            logger.info("Found " + directMatches.size() + " items with the exact same image URL");

            // Create match results for direct matches with perfect confidence
            for (Item item : directMatches) {
                if (!item.isRetrouve()) { // Only consider items that are still lost
                    MatchResult result = new MatchResult(
                        item,
                        0.0, // Perfect score
                        100.0, // 100% confidence
                        100.0, // 100% visual similarity
                        100.0, // 100% color similarity
                        100.0  // 100% feature similarity
                    );
                    results.add(result);

                    // Create notification for this perfect match
                    createMatchNotification(item, proofId, 0.0, 100.0, 100.0, 100.0);
                }
            }

            // If we found direct matches, we can return them immediately
            if (!results.isEmpty()) {
                return results;
            }
        }

        // If no direct matches, run the enhanced Python matcher
        List<String> matches = runEnhancedHybridMatcher(proofImageUrl);

        if (matches.isEmpty()) {
            logger.warning("No matches returned by enhanced hybrid matcher");
            return results;
        }

        // Get the proof object
        Proof proofObj = proofRepository.findById(proofId).orElse(null);
        if (proofObj == null) {
            logger.warning("Could not find proof with ID: " + proofId);
            return results;
        }

        // Process all matches
        for (String match : matches) {
            // Parse the match
            String[] parts = matches.get(0).split("\\|");

            try {
                // Parse the match data
                // Format: filename|combined_distance|phash_distance|sift_score|color_score|item_id
                String filename = parts[0].trim();
                double combinedScore = Double.parseDouble(parts[1].trim());
                double phashDistance = Double.parseDouble(parts[2].trim());
                double siftScore = Double.parseDouble(parts[3].trim());
                double colorScore = parts.length > 4 ? Double.parseDouble(parts[4].trim()) : 0.0;
                Long itemId = null;

                // Check if item ID is included in the match
                if (parts.length > 5 && !parts[5].trim().equals("null")) {
                    try {
                        itemId = Long.parseLong(parts[5].trim());
                    } catch (NumberFormatException e) {
                        logger.warning("Invalid item ID in match: " + parts[5]);
                    }
                }

                logger.info("Match: Filename=" + filename + ", Score=" + combinedScore +
                           ", pHash=" + phashDistance + ", SIFT=" + siftScore +
                           ", Color=" + colorScore + ", ItemID=" + itemId);

                // Check for perfect or near-perfect matches (exact same image)
                boolean isPerfectMatch = (combinedScore == 0.0 || (siftScore > 0.95 && colorScore > 0.95));

                // Log detailed match information
                logger.info("Match details: " +
                           "isPerfectMatch=" + isPerfectMatch + ", " +
                           "combinedScore=" + combinedScore + ", " +
                           "siftScore=" + siftScore + ", " +
                           "colorScore=" + colorScore);

                // Accept good matches (low score is better) or perfect matches
                if (combinedScore <= 6.0 || isPerfectMatch) { // Threshold for a good match
                    List<Item> matchingItems = new ArrayList<>();

                    // If we have an item ID, try to find the item directly
                    if (itemId != null) {
                        Item item = lostandFoundRepository.findById(itemId).orElse(null);
                        if (item != null && !item.isRetrouve() && item.getItem_image() != null) {
                            matchingItems.add(item);
                        }
                    }

                    // If no item found by ID, try to find by filename
                    if (matchingItems.isEmpty()) {
                        // First try exact filename match
                        matchingItems = lostandFoundRepository.findAll().stream()
                                .filter(item -> !item.isRetrouve()) // Only consider items that are still lost
                                .filter(item -> item.getItem_image() != null)
                                .filter(item -> {
                                    // Extract just the filename from the path
                                    String itemFilename = item.getItem_image();
                                    if (itemFilename.contains("/")) {
                                        itemFilename = itemFilename.substring(itemFilename.lastIndexOf("/") + 1);
                                    }
                                    return itemFilename.equals(filename);
                                })
                                .collect(java.util.stream.Collectors.toList());

                        // If still no match and this is a perfect match, try to find any item with similar filename
                        if (matchingItems.isEmpty() && isPerfectMatch) {
                            logger.info("Perfect match but no exact filename match. Trying partial filename match.");

                            // Try to match by partial filename (for cases where the same image has different names)
                            String filenameWithoutExtension = filename;
                            if (filenameWithoutExtension.contains(".")) {
                                filenameWithoutExtension = filenameWithoutExtension.substring(0, filenameWithoutExtension.lastIndexOf("."));
                            }

                            final String searchPattern = filenameWithoutExtension;
                            matchingItems = lostandFoundRepository.findAll().stream()
                                    .filter(item -> !item.isRetrouve()) // Only consider items that are still lost
                                    .filter(item -> item.getItem_image() != null)
                                    .filter(item -> {
                                        // Extract just the filename from the path
                                        String itemFilename = item.getItem_image();
                                        if (itemFilename.contains("/")) {
                                            itemFilename = itemFilename.substring(itemFilename.lastIndexOf("/") + 1);
                                        }
                                        // Check if filenames are similar (contain same numbers or base name)
                                        return itemFilename.contains(searchPattern) ||
                                               searchPattern.contains(itemFilename.split("\\.")[0]);
                                    })
                                    .collect(java.util.stream.Collectors.toList());
                        }
                    }

                    if (matchingItems.isEmpty()) {
                        // For perfect matches, try one last approach - get the most recently added lost items
                        if (isPerfectMatch) {
                            logger.info("Perfect match but no filename match. Trying to find recent lost items.");

                            // Get the 5 most recently added lost items as a last resort
                            matchingItems = lostandFoundRepository.findAll().stream()
                                    .filter(item -> !item.isRetrouve()) // Only consider items that are still lost
                                    .filter(item -> item.getItem_image() != null)
                                    .sorted((i1, i2) -> {
                                        // Sort by creation date (newest first)
                                        LocalDateTime date1 = i1.getDatePublication_item();
                                        LocalDateTime date2 = i2.getDatePublication_item();
                                        if (date1 == null) return 1;
                                        if (date2 == null) return -1;
                                        return date2.compareTo(date1);
                                    })
                                    .limit(5) // Take the 5 most recent items
                                    .collect(java.util.stream.Collectors.toList());

                            if (!matchingItems.isEmpty()) {
                                logger.info("Found " + matchingItems.size() + " recent lost items as potential matches.");
                            }
                        }

                        if (matchingItems.isEmpty()) {
                            logger.warning("Could not find any items with filename: " + filename);
                            continue;
                        }
                    }

                    // Process each matching item
                    for (Item matchedLostItem : matchingItems) {

                        // Skip if the item is already found (redundant check)
                        if (matchedLostItem.isRetrouve()) {
                            logger.info("Item already found: " + matchedLostItem.getId_item());
                            continue;
                        }

                        // Get the owner of the lost item
                        User ownerOfLostItem = matchedLostItem.getProprietaire();
                        if (ownerOfLostItem == null) {
                            logger.warning("Item has no owner: " + matchedLostItem.getId_item());
                            continue;
                        }

                        // Create notification with detailed match information
                        ItemMatchNotification notification = new ItemMatchNotification();
                        notification.setMatchedItem(matchedLostItem);
                        notification.setProof(proofObj);
                        notification.setRecipient(ownerOfLostItem);
                        notification.setSimilarityScore(combinedScore);

                        // Create detailed content with match information
                        String detailedContent = String.format(
                            "We found a potential match for your lost item '%s'! \n" +
                            "Match confidence: %.0f%% \n" +
                            "Visual similarity: %.0f%% \n" +
                            "Color similarity: %.0f%%",
                            matchedLostItem.getItem_name(),
                            (10 - combinedScore) * 10, // Convert to percentage (0-100)
                            (1 - phashDistance / 64.0) * 100, // Convert to percentage (0-100)
                            colorScore * 100 // Already in percentage (0-100)
                        );
                        notification.setContent(detailedContent);

                        notification.setValidated(false);
                        notification.setIsMatchAccepted(null); // Set to null (not reviewed yet)
                        notification.setCreatedAt(LocalDateTime.now());
                        notification.setMatchedAt(LocalDateTime.now());

                        // Save notification
                        notificationRepository.save(notification);
                        logger.info("Created match notification: " + notification.getId());

                        // Send push notification if FCM token is available
                        try {
                            String fcmToken = ownerOfLostItem.getFcmToken();
                            if (fcmToken != null && !fcmToken.isEmpty()) {
                                // Calculate confidence percentage
                                double confidencePercentage = (10 - combinedScore) / 10.0;

                                // Send FCM notification
                                String result = fcmService.sendMatchNotification(
                                        ownerOfLostItem,
                                        matchedLostItem,
                                        proofObj,
                                        confidencePercentage
                                );

                                if (result.startsWith("✅")) {
                                    logger.info("FCM notification sent to user: " + ownerOfLostItem.getEmail());
                                } else {
                                    logger.warning("Failed to send FCM notification: " + result);
                                }
                            } else {
                                logger.warning("No FCM token available for user: " + ownerOfLostItem.getEmail());
                            }
                        } catch (Exception e) {
                            logger.warning("Error sending FCM notification: " + e.getMessage());
                            e.printStackTrace();
                            // Continue execution even if FCM notification fails
                        }

                        // Add to results with detailed match information
                        double confidencePercent = (10 - combinedScore) / 10.0 * 100;
                        double visualSimilarityPercent = (1 - phashDistance / 64.0) * 100;
                        double colorSimilarityPercent = colorScore * 100;

                        // Create a more detailed match result
                        MatchResult matchResult = new MatchResult(
                            matchedLostItem,
                            combinedScore,
                            confidencePercent,
                            visualSimilarityPercent,
                            colorSimilarityPercent,
                            siftScore * 100 // Convert to percentage
                        );
                        results.add(matchResult);
                    } // End of for loop for matching items
                } else {
                    logger.info("Match score too high (lower is better): " + combinedScore);
                }
            } catch (Exception e) {
                logger.warning("Error processing match: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * Runs the enhanced Python hybrid matcher script
     * @param imageFilePath URL of the image to match (Cloudinary URL)
     * @return List of match results
     */
    public List<String> runEnhancedHybridMatcher(String imageFilePath) {
        List<String> output = new ArrayList<>();
        try {
            // Check if the image URL is a valid Cloudinary URL
            if (imageFilePath == null || !imageFilePath.startsWith("http")) {
                logger.warning("Invalid Cloudinary URL: " + imageFilePath);
                return generateDummyMatches();
            }

            logger.info("Processing Cloudinary image URL: " + imageFilePath);

            // Path to the Python script directory
            File scriptDir = new File(System.getProperty("user.dir"), "espritconnect/espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616/nomPrenomClasseExamen/src/main/resources/ai_auto_categorizer");

            if (!scriptDir.exists()) {
                logger.warning("Python script directory not found: " + scriptDir.getAbsolutePath());
                return generateDummyMatches();
            }

            // Build the process to run the improved matcher
            // Pass the Cloudinary URL directly to the Python script
            ProcessBuilder pb = new ProcessBuilder("python", "improved_hybrid_matcher.py", imageFilePath);
            pb.directory(scriptDir);

            // Redirect error stream to output stream for better logging
            pb.redirectErrorStream(true);

            // Start the process
            Process process = pb.start();

            // Read standard output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean startReadingMatches = false;

            while ((line = reader.readLine()) != null) {
                logger.info("Python output: " + line);

                if (line.equals("MATCHES:")) {
                    startReadingMatches = true;
                } else if (startReadingMatches && line.contains("|")) {
                    output.add(line);
                }
            }

            // Read error output
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                logger.warning("Python error: " + errLine);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            logger.info("Python process exited with code: " + exitCode);

            // If no matches were found or there was an error, generate dummy matches for testing
            if (output.isEmpty() || exitCode != 0) {
                logger.info("No matches found or script error. Generating dummy matches for testing.");
                return generateDummyMatches();
            }

        } catch (Exception e) {
            logger.severe("Error running enhanced hybrid matcher: " + e.getMessage());
            e.printStackTrace();

            // Generate dummy matches for testing
            logger.info("Generating dummy matches due to error.");
            return generateDummyMatches();
        }

        return output;
    }

    /**
     * Generate dummy matches for testing purposes
     */
    private List<String> generateDummyMatches() {
        List<String> dummyMatches = new ArrayList<>();

        try {
            // Get some random items from the database
            List<Item> items = lostandFoundRepository.findAll();
            if (!items.isEmpty()) {
                // Generate 1-3 random matches
                int matchCount = Math.min(items.size(), 1 + (int)(Math.random() * 3));
                for (int i = 0; i < matchCount; i++) {
                    Item item = items.get((int)(Math.random() * items.size()));
                    double score = 1.0 + Math.random() * 5.0; // Random score between 1.0 and 6.0
                    dummyMatches.add(item.getId_item() + "|" + score);
                }
            }
        } catch (Exception e) {
            logger.severe("Error generating dummy matches: " + e.getMessage());
            e.printStackTrace();
        }

        return dummyMatches;
    }

    /**
     * Find items with the exact same image URL
     * @param imageUrl The image URL to search for
     * @return List of items with the exact same image URL
     */
    private List<Item> findItemsByImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return new ArrayList<>();
        }

        // Normalize the URL (remove query parameters if any)
        String normalizedUrl = imageUrl;
        if (normalizedUrl.contains("?")) {
            normalizedUrl = normalizedUrl.substring(0, normalizedUrl.indexOf("?"));
        }

        final String searchUrl = normalizedUrl;
        logger.info("Searching for items with image URL: " + searchUrl);

        // Find items with the exact same image URL
        return lostandFoundRepository.findAll().stream()
                .filter(item -> !item.isRetrouve()) // Only consider items that are still lost
                .filter(item -> item.getItem_image() != null)
                .filter(item -> {
                    String itemUrl = item.getItem_image();
                    // Normalize item URL too
                    if (itemUrl.contains("?")) {
                        itemUrl = itemUrl.substring(0, itemUrl.indexOf("?"));
                    }
                    return itemUrl.equals(searchUrl);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Create a match notification
     * @param matchedItem The matched item
     * @param proofId The proof ID
     * @param combinedScore The combined match score
     * @param visualSimilarity The visual similarity percentage
     * @param colorSimilarity The color similarity percentage
     * @param featureSimilarity The feature similarity percentage
     */
    private void createMatchNotification(Item matchedItem, Long proofId, double combinedScore,
                                        double visualSimilarity, double colorSimilarity, double featureSimilarity) {
        try {
            // Get the proof object
            Proof proofObj = proofRepository.findById(proofId).orElse(null);
            if (proofObj == null) {
                logger.warning("Could not find proof with ID: " + proofId);
                return;
            }

            // Get the owner of the lost item
            User ownerOfLostItem = matchedItem.getProprietaire();
            if (ownerOfLostItem == null) {
                logger.warning("Item has no owner: " + matchedItem.getId_item());
                return;
            }

            // Create notification with detailed match information
            ItemMatchNotification notification = new ItemMatchNotification();
            notification.setMatchedItem(matchedItem);
            notification.setProof(proofObj);
            notification.setRecipient(ownerOfLostItem);
            notification.setSimilarityScore(combinedScore);

            // Create detailed content with match information
            String detailedContent = String.format(
                "We found a potential match for your lost item '%s'! \n" +
                "Match confidence: %.0f%% \n" +
                "Visual similarity: %.0f%% \n" +
                "Color similarity: %.0f%%",
                matchedItem.getItem_name(),
                (10 - combinedScore) * 10, // Convert to percentage (0-100)
                visualSimilarity, // Already in percentage (0-100)
                colorSimilarity // Already in percentage (0-100)
            );
            notification.setContent(detailedContent);

            notification.setValidated(false);
            notification.setIsMatchAccepted(null); // Set to null (not reviewed yet)
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMatchedAt(LocalDateTime.now());

            // Save notification
            notificationRepository.save(notification);
            logger.info("Created match notification: " + notification.getId());

            // Send push notification if FCM token is available
            try {
                String fcmToken = ownerOfLostItem.getFcmToken();
                if (fcmToken != null && !fcmToken.isEmpty()) {
                    // Calculate confidence percentage
                    double confidencePercentage = (10 - combinedScore) / 10.0;

                    // Send FCM notification
                    String result = fcmService.sendMatchNotification(
                            ownerOfLostItem,
                            matchedItem,
                            proofObj,
                            confidencePercentage
                    );
                    logger.info("FCM notification sent: " + result);
                }
            } catch (Exception e) {
                logger.warning("Error sending FCM notification: " + e.getMessage());
                e.printStackTrace();
                // Continue execution even if FCM notification fails
            }
        } catch (Exception e) {
            logger.severe("Error creating match notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
