package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.IFoyerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.NotificationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.PreferenceRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ServicesFoyerImpl implements IFoyerServices {



    @Autowired
    private NotificationFEmitterService emitterService;

    private final CloudinaryService cloudinaryService;
    private final IFoyerRepository foyerRepository;
    private final NotificationFRepository notificationRepository;
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final NotificationServiceImpl notificationService;
    private final Cloudinary cloudinary;
    @Autowired
    public ServicesFoyerImpl(
            IFoyerRepository foyerRepository,
            NotificationFRepository notificationRepository,
            PreferenceRepository preferenceRepository,
            UserRepository userRepository,
            CloudinaryService cloudinaryService,
            Cloudinary cloudinary// 👈 Ajouter ceci
    ) {
        this.foyerRepository = foyerRepository;
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.notificationService = new NotificationServiceImpl();
        this.cloudinary = cloudinary;
        // 👈 Initialiser ici
    }

    @Override
    public Foyer updateFoyer(Long id, Foyer updatedFoyer, MultipartFile image) {
        Optional<Foyer> optionalFoyer = foyerRepository.findById(id);
        if (optionalFoyer.isPresent()) {
            Foyer existingFoyer = optionalFoyer.get();

            // 🛠️ Mise à jour des champs
            existingFoyer.setDescription(updatedFoyer.getDescription());
            existingFoyer.setLocalisation(updatedFoyer.getLocalisation());
            existingFoyer.setLatitude(updatedFoyer.getLatitude());
            existingFoyer.setLongitude(updatedFoyer.getLongitude());
            existingFoyer.setSuperficie(updatedFoyer.getSuperficie());
            existingFoyer.setPrix(updatedFoyer.getPrix());
            existingFoyer.setNbrDeChambre(updatedFoyer.getNbrDeChambre());
            existingFoyer.setTypeM(updatedFoyer.getTypeM());
            existingFoyer.setMeuble(updatedFoyer.getMeuble());
            existingFoyer.setGoogleMapsLink(updatedFoyer.getGoogleMapsLink());

            // 🖼️ Gestion de l'image
            if (image != null && !image.isEmpty()) {
                try {
                    if (existingFoyer.getImage() != null && !existingFoyer.getImage().isEmpty()) {
                        String publicId = extractPublicIdFromUrl(existingFoyer.getImage());
                        cloudinaryService.deleteFile(publicId);
                    }
                    String newImageUrl = cloudinaryService.uploadFile(image, "foyers");
                    existingFoyer.setImage(newImageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de l'upload de l'image : " + e.getMessage());
                }
            }

            // 🔁 Mise à jour du statut
            existingFoyer.setStatus(updatedFoyer.getStatus());
            Foyer savedFoyer = foyerRepository.save(existingFoyer);

            // 🔔 Notification propriétaire
            NotificationF notifOwner = new NotificationF();
            notifOwner.setFoyer(savedFoyer);
            notifOwner.setUser(savedFoyer.getUser());
            notifOwner.setLu(false);
            notifOwner.setMessage(
                    savedFoyer.getStatus() == Status.CONFIRME
                            ? "Votre foyer a été confirmé par l'administration."
                            : "Votre foyer a été rejeté par l'administration."
            );
            notificationRepository.save(notifOwner);
            emitterService.sendNotification(notifOwner.getUser().getId(), notifOwner); // ✅ Envoi SSE

            // 🔔 Notifications utilisateurs avec préférences (si confirmé)
            if (savedFoyer.getStatus() == Status.CONFIRME) {
                List<Preference> allPreferences = preferenceRepository.findAll();
                for (Preference pref : allPreferences) {
                    if (pref.getLatitude() != null && pref.getLongitude() != null) {
                        double distance = GeoUtils.distanceInKm(
                                savedFoyer.getLatitude(), savedFoyer.getLongitude(),
                                pref.getLatitude(), pref.getLongitude()
                        );

                        boolean prixValide = savedFoyer.getPrix() >= pref.getPrixMin()
                                && savedFoyer.getPrix() <= pref.getPrixMax();

                        if (distance < 2 && prixValide) {
                            NotificationF notif = new NotificationF();
                            notif.setMessage("Un foyer correspondant à votre préférence a été publié.");
                            notif.setLu(false);
                            notif.setFoyer(savedFoyer);
                            notif.setUser(pref.getUser());
                            notificationRepository.save(notif);
                            emitterService.sendNotification(notif.getUser().getId(), notif); // ✅ Envoi SSE
                        }
                    }
                }
            }

            return savedFoyer;
        }

        return null;
    }


    private String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }


    public static class GeoUtils {
        public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
            final int R = 6371;
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        }
    }


    @Override
    public List<Foyer> getAllFoyers() {
        return foyerRepository.findAll();
    }



    @Override
    public Foyer uploadFoyer(Foyer foyer, MultipartFile image) {
        try {
            String imageUrl = cloudinaryService.uploadFile(image, "foyer");
            foyer.setImage(imageUrl);
            return foyerRepository.save(foyer);
        } catch (Exception e) {
            throw new RuntimeException("Échec du téléversement de l'image", e);
        }
    }


    @Override
    public List<Foyer> searchFoyersByLocation(String location) {
        return foyerRepository.findByLocalisationStartingWith(location);
    }

    @Override
    public List<Foyer> getFoyersByCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
        return foyerRepository.findByUser(user);
    }

    @Override
    public void deleteFoyer(Long id) {
        foyerRepository.deleteById(id);
    }

    public List<Foyer> searchFoyersByPrice(Double prixMin, Double prixMax) {
        if (prixMin != null && prixMax != null) {
            return foyerRepository.findByPrixBetween(prixMin, prixMax);
        } else if (prixMin != null) {
            return foyerRepository.findByPrixGreaterThanEqual(prixMin);
        } else if (prixMax != null) {
            return foyerRepository.findByPrixLessThanEqual(prixMax);
        } else {
            return foyerRepository.findAll();
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public Foyer getFoyerById(Long id) {
        return foyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foyer non trouvé avec l'ID : " + id));
    }
}