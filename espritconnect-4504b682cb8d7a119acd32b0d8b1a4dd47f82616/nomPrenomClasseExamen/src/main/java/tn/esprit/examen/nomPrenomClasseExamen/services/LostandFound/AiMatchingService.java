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

@Service
public class AiMatchingService {
    private static final Logger logger = Logger.getLogger(AiMatchingService.class.getName());
    @Autowired
    private LostandFoundRepository itemRepository;
    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private ItemMatchNotificationRepository notificationRepository;
    @Autowired
    private FCMService fcmService;

    /**
     * Result class for matches
     */
    public static class MatchResult {
        private Item item;
        private double score;
        private double confidencePercent;

        public MatchResult(Item item, double score, double confidencePercent) {
            this.item = item;
            this.score = score;
            this.confidencePercent = confidencePercent;
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
     * @param proofImagePath URL of the proof image
     * @param proofId ID of the proof
     * @return List of match results
     */
    private List<MatchResult> findMatchesInternal(String proofImagePath, Long proofId) {
        List<String> matches = runHybridMatcher(proofImagePath);
        List<MatchResult> results = new ArrayList<>();

        if (matches.isEmpty()) {
            logger.warning("No matches returned by hybrid matcher");
            return results;
        }

        // Process matches
            String[] best = matches.get(0).split("\\|");
            String filename = best[0].trim();
            int score = Integer.parseInt(best[1].trim());

            String matchedName = Paths.get(filename).getFileName().toString();

            List<Item> lostItems = itemRepository.findAll().stream()
                    .filter(item -> !item.isRetrouve())
                    .filter(item -> item.getItem_image() != null)
                    .filter(item -> {
                        String dbFilename = Paths.get(item.getItem_image()).getFileName().toString();
                        return dbFilename.equalsIgnoreCase(matchedName);
                    })
                    .toList();

            if (!lostItems.isEmpty() && score < 10) {
                Item matchedLostItem = lostItems.get(0);
                User ownerOfLostItem = matchedLostItem.getProprietaire(); // 👈 FIXED

                ItemMatchNotification notification = new ItemMatchNotification();
                notification.setRecipient(ownerOfLostItem);
                notification.setContent("We found a match for your lost item!");
                notification.setCreatedAt(LocalDateTime.now());
                notification.setSeen(false);
                Proof proofObj = proofRepository.findById(proofId).orElse(null);
                notification.setProof(proofObj);
                notification.setMatchedItem(matchedLostItem);
                notification.setMatchedAt(LocalDateTime.now());

                notificationRepository.save(notification);

                // Send push notification if FCM token is available
                try {
                    if (ownerOfLostItem != null) {
                        String fcmToken = ownerOfLostItem.getFcmToken();
                        if (fcmToken != null && !fcmToken.isEmpty()) {
                            // Calculate confidence percentage
                            double confidencePercentage = (10 - score) / 10.0;

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
                    } else {
                        logger.warning("Owner of lost item is null, cannot send FCM notification");
                    }
                } catch (Exception e) {
                    logger.warning("Error sending FCM notification: " + e.getMessage());
                    e.printStackTrace();
                    // Continue execution even if FCM notification fails
                }

                // Add to results
                double confidencePercent = (10 - score) / 10.0 * 100;
                results.add(new MatchResult(matchedLostItem, score, confidencePercent));

                // FCM notification already sent above
            } else {
                logger.warning("No matching item found or score too high: " + score);
            }
        return results;

    }

    public List<String> runHybridMatcher(String imageFilePath) {
        List<String> output = new ArrayList<>();
        try {
            File scriptDir = new File("C:\\Users\\Tifa\\Desktop\\PiSpring\\espritconnect\\espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616\\nomPrenomClasseExamen\\ai_auto_categorizer");
            String proofImagePath = new File(imageFilePath).getAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder("python", "enhanced_hybrid_matcher.py", proofImagePath);
            pb.directory(scriptDir);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            boolean startReadingMatches = false;

            while ((line = reader.readLine()) != null) {
                if (line.equals("MATCHES:")) {
                    startReadingMatches = true;
                } else if (startReadingMatches && line.contains("|")) {
                    output.add(line);
                }
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                System.err.println("🐍 PYTHON ERROR: " + errLine);
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

}