package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.ItemMatchNotification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ItemMatchNotificationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.FCMService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceLostandFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/item")
public class LostandFoundController {

    private final String uploadDir;

    @Autowired
    private IServiceLostandFound serviceLostItem;

    private LostandFoundRepository itemRepository;
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private ItemMatchNotificationRepository notificationRepository;

    @Autowired
    public LostandFoundController(@Value("${upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    // GET: Retrieve all lost items
    @GetMapping("/all")
    public List<Item> getLostItems() {
        return serviceLostItem.retrieveAllLostItems();
    }



    // POST: Add a new lost item
    @PostMapping("/add")
    public ResponseEntity<Item> addLostItem(@RequestBody Item item) {

        // 🔐 Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // email

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        item.setProprietaire(currentUser);
        // 📦 Retrieve the user from DB


        // ✅ Add item using service
        Item savedItem = serviceLostItem.addLostItem(item);
        return ResponseEntity.ok(savedItem);
        // ← ensure this line is there!

    }

    // DELETE: Remove a lost item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        serviceLostItem.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // PUT: Modify an existing lost item
    @PutMapping("/update/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("datePerdu") String datePerdu,
            @RequestParam("lieuPerdu") String lieuPerdu,
            @RequestParam("retrouve") boolean retrouve,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        Item updatedItem = serviceLostItem.updateItem(id, name, description, datePerdu, lieuPerdu, retrouve, image);
        return ResponseEntity.ok(updatedItem);
    }


    @GetMapping("/category/{cat}")
    public List<Item> getItemsByCategory(@PathVariable("cat") CategoryItem category) {
        return itemRepository.findByCategory(category);
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> handleItemUpload(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("datePerdu") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePerdu,
            @RequestParam("lieuPerdu") String lieuPerdu,
            @RequestParam("retrouve") boolean retrouve,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        Object result = serviceLostItem.uploadItemOrProof(name, description, datePerdu, lieuPerdu, retrouve, imageFile);
        return ResponseEntity.ok(result);
    }

    // 👇️ Put this **AFTER** /upload
    @GetMapping("/{item-id}")
    public Item retrieveLostItem(@PathVariable("item-id") Long itemId) {
        return serviceLostItem.retrieveLostItem(itemId);
    }

    /**
     * Get all match notifications for the authenticated user
     * @return List of match notifications
     */
    @GetMapping("/matches")
    public List<ItemMatchNotification> getUserMatchNotifications() {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // email

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get all notifications for the user, ordered by creation date (newest first)
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId());
    }

}
    // GET: Find items by location

    // GET: Find unfound items
    //@GetMapping("/unfound-items")
    //public List<Item> findUnfoundItems() {
        //     return serviceLostItem.findUnfoundItems();
    //}

