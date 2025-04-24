package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceLostandFoundImpl implements IServiceLostandFound {

    @Autowired
    private LostandFoundRepository lostItemRepository;
    @Autowired
    private AiCategoryService aiCategoryService;
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

        // Predict category using AI
        String predicted = aiCategoryService.predictCategory(item.getItem_image());

        if (predicted != null) {
            try {
                item.setCategory(CategoryItem.valueOf(predicted));
                System.out.println("✅ Category set to: " + predicted);
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Invalid category from AI: " + predicted);
            }
        } else {
            System.out.println("⚠️ Prediction returned null or UNKNOWN");
        }

        Item saved = itemRepository.save(item);

        // Run match logic only if this is a FOUND item
        //if (item.isRetrouve()) {
        //   aiMatcherService.runMatchAndNotifyIfFound(saved); // <-- matching + notification logic here
        //}

        return saved;
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
            String uploadDir = Paths.get(basePath, subFolder).toString();

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String subFolder = retrouve ? "proof" : "items";
        String imageUrl = null;

        // Upload to Cloudinary in correct folder
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Uploading image to Cloudinary folder: " + subFolder);
            imageUrl = cloudinaryServicee.uploadFile(imageFile, "uploads/" + subFolder);
            System.out.println("✅ Image uploaded, URL: " + imageUrl);
        }

        // Run category classification on uploaded image (only for lost items)
        String category = !retrouve && imageUrl != null
                ? aiCategoryService.predictCategory(imageUrl)
                : null;

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
            if (category != null) {
                try {
                    item.setCategory(CategoryItem.valueOf(category.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Unknown category predicted: " + category);
                }
            }

            return ResponseEntity.ok(itemRepository.save(item));
        } else {
            Proof proof = new Proof();
            proof.setImage_url(imageUrl);
            proof.setDescription(description);
            proof.setDateSubmitted(LocalDateTime.now());
            proof.setValidated(false);
            proof.setProprietaire(currentUser);

            proofRepository.save(proof);

            // Trigger matching AI on the proof
            aiMatcherService.findMatches(proof);

            return ResponseEntity.ok().build();
        }
    }

}



