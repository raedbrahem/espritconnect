package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.PreferenceRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PreferenceServiceImpl implements IPreferenceService {

    @Autowired
    private PreferenceRepository prefRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Preference> getAllPreferences() {
        return prefRepository.findAll();
    }

    @Override
    public Preference getPreferenceById(Long id) {
        return prefRepository.findById(id).orElse(null);
    }

    @Override
    public Preference addPreference(Preference preference) {
        if (preference == null) {
            throw new IllegalArgumentException("Preference cannot be null");
        }

        if (preference.getLocalisation() == null || preference.getLocalisation().isEmpty()) {
            throw new IllegalArgumentException("Localisation cannot be empty");
        }

        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));

        preference.setUser(user);
        return prefRepository.save(preference);
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    @Override
    public Preference updatePreference(Long id, Preference preference) {
        return prefRepository.findById(id).map(existing -> {
            if (preference.getPrixMin() != null) existing.setPrixMin(preference.getPrixMin());
            if (preference.getPrixMax() != null) existing.setPrixMax(preference.getPrixMax());
            if (preference.getTypeM() != null) existing.setTypeM(preference.getTypeM());
            if (preference.getLocalisation() != null) existing.setLocalisation(preference.getLocalisation());
            if (preference.getLatitude() != null) {
                existing.setLatitude(preference.getLatitude());
            }
            if (preference.getLongitude() != null) {
                existing.setLongitude(preference.getLongitude());
            }
            return prefRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Preference with id " + id + " not found"));
    }

    @Override
    @Transactional
    public void deletePreference(Long id) {
        Preference preference = prefRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preference with id " + id + " not found"));

        // Rompre la liaison côté User
        User user = preference.getUser();
        if (user != null) {
            user.setPreference(null);
        }

        // Maintenant, on peut supprimer
        prefRepository.delete(preference);
    }


    @Override
    public Preference getPreferenceByCurrentUser() {
        String email = getCurrentUserEmail();  // Récupérer l'email de l'utilisateur connecté
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));

        // Récupérer la préférence de l'utilisateur, si elle existe
        return prefRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Aucune préférence trouvée pour l'utilisateur : " + email));
    }

}
