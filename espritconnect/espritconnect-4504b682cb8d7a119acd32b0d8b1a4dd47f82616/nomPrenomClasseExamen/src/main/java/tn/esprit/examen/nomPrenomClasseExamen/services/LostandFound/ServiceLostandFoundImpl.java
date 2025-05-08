package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceLostandFoundImpl implements IServiceLostandFound {

    @Autowired
    private LostandFoundRepository lostItemRepository;
    @Autowired
    private CategoryPredictionService categoryPredictionService;
    @Autowired
    private LostandFoundRepository itemRepository;

    @Autowired
    private EnhancedAiMatchingService aiMatcherService;
    @Autowired
    private  CloudinaryServicee cloudinaryServicee;

    private final ProofRepository proofRepository;
    private final UserRepository userRepository;



    public ServiceLostandFoundImpl(
            LostandFoundRepository itemRepository,
            ProofRepository proofRepository,
            UserRepository userRepository
    ) {
        this.itemRepository = itemRepository;
        this.proofRepository = proofRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> retrieveAllLostItems() {
        return lostItemRepository.findAll();
    }

    @Override
    public Item retrieveLostItem(Long id) {
        return lostItemRepository.findById(id).orElse(null);
    }

    @Override
    public Item addLostItem(Item item) {
        System.out.println("🚀 Adding item: " + item.getItem_name());
        item.setDatePublication_item(LocalDateTime.now());

        // Predict category using AI with the CategoryPredictionService
        try {
            // Check if the item image is a URL or a file path
            if (item.getItem_image() != null && item.getItem_image().startsWith("http")) {
                // For Cloudinary URLs, use the new method to predict category from URL
                System.out.println("Image is a URL, using predictCategoryFromUrl");
                CategoryPredictionService.CategoryPrediction prediction =
                    categoryPredictionService.predictCategoryFromUrl(item.getItem_image());

                if (prediction != null && !"Other".equals(prediction.getCategory())) {
                    try {
                        item.setCategory(CategoryItem.valueOf(prediction.getCategory().toUpperCase()));
                        System.out.println("✅ Category set to: " + prediction.getCategory() +
                                          " with confidence: " + prediction.getConfidence());
                    } catch (IllegalArgumentException e) {
                        System.err.println("❌ Invalid category from AI: " + prediction.getCategory());
                    }
                } else {
                    System.out.println("⚠️ Prediction returned Other or null");
                }
            } else if (item.getItem_image() != null) {
                // For local file paths, we can use the file directly
                File imageFile = new File(item.getItem_image());
                if (imageFile.exists()) {
                    // Convert File to MultipartFile
                    MultipartFile multipartFile = convertFileToMultipartFile(imageFile);
                    if (multipartFile != null) {
                        CategoryPredictionService.CategoryPrediction prediction =
                            categoryPredictionService.predictCategory(multipartFile);

                        if (prediction != null && !"Other".equals(prediction.getCategory())) {
                            try {
                                item.setCategory(CategoryItem.valueOf(prediction.getCategory().toUpperCase()));
                                System.out.println("✅ Category set to: " + prediction.getCategory() +
                                                  " with confidence: " + prediction.getConfidence());
                            } catch (IllegalArgumentException e) {
                                System.err.println("❌ Invalid category from AI: " + prediction.getCategory());
                            }
                        } else {
                            System.out.println("⚠️ Prediction returned Other or null");
                        }
                    }
                } else {
                    System.out.println("⚠️ Image file does not exist: " + item.getItem_image());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error predicting category: " + e.getMessage());
            e.printStackTrace();
        }

        Item saved = itemRepository.save(item);

        // Run match logic only if this is a FOUND item
        //if (item.isRetrouve()) {
        //   aiMatcherService.runMatchAndNotifyIfFound(saved); // <-- matching + notification logic here
        //}

        return saved;
    }

    /**
     * Convert a File to a MultipartFile
     */
    private MultipartFile convertFileToMultipartFile(File file) {
        try {
            return new MultipartFile() {
                @Override
                public String getName() {
                    return file.getName();
                }

                @Override
                public String getOriginalFilename() {
                    return file.getName();
                }

                @Override
                public String getContentType() {
                    return "image/jpeg"; // Assume JPEG for simplicity
                }

                @Override
                public boolean isEmpty() {
                    return file.length() == 0;
                }

                @Override
                public long getSize() {
                    return file.length();
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return Files.readAllBytes(file.toPath());
                }

                @Override
                public java.io.InputStream getInputStream() throws IOException {
                    return Files.newInputStream(file.toPath());
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            };
        } catch (Exception e) {
            System.err.println("❌ Error converting file to MultipartFile: " + e.getMessage());
            return null;
        }
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }


    @Override
    public Item updateItem(Long id, String name, String description, String datePerdu, String lieuPerdu, boolean retrouve, MultipartFile imageFile) throws IOException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        item.setItem_name(name);
        item.setDescription(description);
        item.setDatePerdu(LocalDate.parse(datePerdu));
        item.setLieuPerdu(lieuPerdu);
        item.setRetrouve(retrouve);

        if (imageFile != null && !imageFile.isEmpty()) {

            String basePath = new File("uploads").getAbsolutePath();
            String subFolder = "items"; // Assuming this is for items only
            String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "items").toString();

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            File destination = new File(uploadDir, fileName);
            imageFile.transferTo(destination);

            item.setItem_image(fileName);
        }

        return itemRepository.save(item);
    }


    @Override
    public ResponseEntity<Object> uploadItemOrProof(
            String name,
            String description,
            LocalDate datePerdu,
            String lieuPerdu,
            boolean retrouve,
            MultipartFile imageFile
    ) throws IOException {
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //User currentUser = userRepository.findByEmail(auth.getName())
         //       .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String subFolder = retrouve ? "proof" : "items";
        String imageUrl = null;

        // Upload to Cloudinary in correct folder
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Uploading image to Cloudinary folder: " + subFolder);
            imageUrl = cloudinaryServicee.uploadFile(imageFile, "uploads/" + subFolder);
            System.out.println("✅ Image uploaded, URL: " + imageUrl);
        }

        // Run category classification on uploaded image (only for lost items)
        CategoryPredictionService.CategoryPrediction prediction = null;
        if (!retrouve && imageUrl != null) {
            try {
                // Use the new method to predict category from Cloudinary URL
                prediction = categoryPredictionService.predictCategoryFromUrl(imageUrl);
                System.out.println("✅ Category prediction from URL: " + prediction.getCategory() +
                                  " with confidence: " + prediction.getConfidence());
            } catch (Exception e) {
                System.err.println("❌ Error predicting category from URL: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (!retrouve) {
            Item item = new Item();
            item.setItem_name(name);
            item.setDescription(description);
            item.setDatePerdu(datePerdu);
            item.setLieuPerdu(lieuPerdu);
            item.setRetrouve(false);
            item.setItem_image(imageUrl);
            item.setDatePublication_item(LocalDateTime.now());
            item.setProprietaire(currentUser);

            // Set predicted category
            if (prediction != null && !"Other".equals(prediction.getCategory())) {
                try {
                    item.setCategory(CategoryItem.valueOf(prediction.getCategory().toUpperCase()));
                    System.out.println("✅ Category set to: " + prediction.getCategory());
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Unknown category predicted: " + prediction.getCategory());
                }
            } else {
                System.out.println("⚠️ No valid category predicted, using default");
            }

            return ResponseEntity.ok(itemRepository.save(item));
        } else {
            Proof proof = new Proof();
            proof.setImage_url(imageUrl);
            proof.setDescription(description);
            proof.setDateSubmitted(LocalDateTime.now());
            proof.setValidated(false);
            proof.setProprietaire(currentUser);
            proof.setName(name);
            proof.setLieuPerdu(lieuPerdu);


            proofRepository.save(proof);

            // Trigger matching AI on the proof
            aiMatcherService.findMatches(proof);

            return ResponseEntity.ok().build();
        }
    }

}



