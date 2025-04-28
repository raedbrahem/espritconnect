package tn.esprit.examen.nomPrenomClasseExamen.controllers.Foyer;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.NotificationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.PreferenceRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.ReservationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CloudinaryService;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.CohereService;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.IFoyerServices;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.NotificationFEmitterService;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.ServicesFoyerImpl;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.IFoyerRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/foyers")
public class FoyerRestController {


    @Autowired
    private IFoyerRepository foyerRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private ReservationFRepository reservationRepo;


    @Autowired
    private NotificationFEmitterService emitterService ;

    @Autowired
    private IFoyerServices foyerService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private NotificationFRepository notificationRepository;


    @Autowired
    private UserService userService;

    @Autowired
    private CohereService cohereService;

    @GetMapping("/{id}/generate-description")
    public String generateFoyerDescription(@PathVariable Long id) throws IOException {
        Optional<Foyer> optionalFoyer = foyerRepository.findById(id);
        if (optionalFoyer.isPresent()) {
            Foyer foyer = optionalFoyer.get();
            return cohereService.generateFoyerDescription(foyer);
        } else {
            throw new RuntimeException("Foyer non trouvé avec l'ID : " + id);
        }
    }


    private final String uploadDir = "C:/Users/ASUS/OneDrive - ESPRIT/Bureau/PI-Pull-back/espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616/nomPrenomClasseExamen/uploads/foyers";



    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Récupérer tous les foyers
    @GetMapping
    public List<Foyer> getAllFoyers() {
        return foyerService.getAllFoyers();
    }


    // Supprimer un foyer
    @DeleteMapping("/delete/{id}")
    public void deleteFoyer(@PathVariable Long id) {
        foyerService.deleteFoyer(id);
    }

    @GetMapping("/by-foyer/{foyerId}")
    public List<ReservationF> getReservationsByFoyer(@PathVariable Long foyerId) {
        return reservationRepo.findByFoyerId(foyerId);
    }

    @PostMapping("/add")
    public ResponseEntity<Foyer> addFoyer(
            @RequestParam("description") String description,
            @RequestParam("localisation") String localisation,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("superficie") Double superficie,
            @RequestParam("prix") Double prix,
            @RequestParam("nbrDeChambre") Integer nbrDeChambre,
            @RequestParam TypeM typeM,
            @RequestParam("meuble") Boolean meuble,
            @RequestParam("googleMapsLink") String googleMapsLink,
            @RequestParam("image") MultipartFile image) {

        // 1. Get the authenticated user (owner of the Foyer)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> optionalUser = userService.findByEmail(email);
        User user = optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 2. Create foyer object
        Foyer foyer = new Foyer();
        foyer.setDescription(description);
        foyer.setLocalisation(localisation);
        foyer.setLatitude(latitude);
        foyer.setLongitude(longitude);
        foyer.setSuperficie(superficie);
        foyer.setPrix(prix);
        foyer.setNbrDeChambre(nbrDeChambre);
        foyer.setTypeM(typeM);
        foyer.setMeuble(meuble);
        foyer.setGoogleMapsLink(googleMapsLink);
        foyer.setUser(user); // The authenticated user is set as the owner of the Foyer

        // 3. Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadFile(image, "foyer");
        System.out.println("Image uploaded, URL: " + imageUrl);
        foyer.setImage(imageUrl);

        // 4. Save foyer
        Foyer createdFoyer = foyerService.uploadFoyer(foyer, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFoyer);
    }



    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Détection du type MIME
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFoyer(
            @PathVariable Long id,
            @RequestParam("description") String description,
            @RequestParam("localisation") String localisation,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("superficie") Double superficie,
            @RequestParam("prix") Double prix,
            @RequestParam("nbrDeChambre") Integer nbrDeChambre,
            @RequestParam("typeM") String typeM,
            @RequestParam("meuble") Boolean meuble,
            @RequestParam("googleMapsLink") String googleMapsLink,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // Récupérer l'entité Foyer existante
            Foyer foyerToUpdate = foyerService.getFoyerById(id);

            if (foyerToUpdate == null) {
                return ResponseEntity.notFound().build(); // Si le foyer n'est pas trouvé
            }

            // Mettre à jour les propriétés
            foyerToUpdate.setDescription(description);
            foyerToUpdate.setLocalisation(localisation);
            foyerToUpdate.setLatitude(latitude);
            foyerToUpdate.setLongitude(longitude);
            foyerToUpdate.setSuperficie(superficie);
            foyerToUpdate.setPrix(prix);
            foyerToUpdate.setNbrDeChambre(nbrDeChambre);
            foyerToUpdate.setTypeM(TypeM.valueOf(typeM)); // Enum vérifié
            foyerToUpdate.setMeuble(meuble);
            foyerToUpdate.setGoogleMapsLink(googleMapsLink);

            // Gérer l'image
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadFile(image, "foyers"); // Upload de l'image
                foyerToUpdate.setImage(imageUrl); // Mise à jour de l'image du foyer
            }

            // Sauvegarder les changements dans la base de données
            Foyer updatedFoyer = foyerService.updateFoyer(id, foyerToUpdate, image);

            return ResponseEntity.ok(updatedFoyer);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur : " + e.getMessage());
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Foyer>> searchFoyersByLocation(@RequestParam("location") String location) {
        List<Foyer> filteredFoyers = foyerService.searchFoyersByLocation(location);
        return ResponseEntity.ok(filteredFoyers);
    }

    // Ajouter un filtre par prix min et prix max
    @GetMapping("/searchByPrice")
    public ResponseEntity<List<Foyer>> searchFoyersByPrice(
            @RequestParam(value = "prixMin", required = false) Double prixMin,
            @RequestParam(value = "prixMax", required = false) Double prixMax
    ) {
        List<Foyer> filteredFoyers = foyerService.searchFoyersByPrice(prixMin, prixMax);
        return ResponseEntity.ok(filteredFoyers);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateFoyerStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Foyer foyer = foyerService.getFoyerById(id);
            if (foyer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Foyer non trouvé");
            }

            try {
                foyer.setStatus(Status.valueOf(status.toUpperCase())); // Conversion du status en enum
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Status invalide");
            }

            Foyer updated = foyerService.updateFoyer(id, foyer, null); // Mise à jour sans image
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Foyer> getFoyerById(@PathVariable Long id) {
        try {
            Foyer foyer = foyerService.getFoyerById(id);
            return ResponseEntity.ok(foyer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/my-foyers")
    public ResponseEntity<List<Foyer>> getFoyersByCurrentUser() {
        List<Foyer> foyers = foyerService.getFoyersByCurrentUser();
        return ResponseEntity.ok(foyers);
    }

    @GetMapping("/confirmed-reservations/{foyerId}")
    public List<Map<String, LocalDate>> getConfirmedReservationsByFoyer(@PathVariable Long foyerId) {
        List<ReservationF> confirmedReservations = reservationRepo.findByFoyerIdAndStatut(foyerId, ReservationF.StatutReservation.CONFIRMEE);

        // Créer une liste de maps contenant les dates de début et de fin
        return confirmedReservations.stream()
                .map(reservation -> {
                    Map<String, LocalDate> dateMap = new HashMap<>();
                    dateMap.put("dateDebut", reservation.getDateDebut());
                    dateMap.put("dateFin", reservation.getDateFin());
                    return dateMap;
                })
                .collect(Collectors.toList());
    }


}