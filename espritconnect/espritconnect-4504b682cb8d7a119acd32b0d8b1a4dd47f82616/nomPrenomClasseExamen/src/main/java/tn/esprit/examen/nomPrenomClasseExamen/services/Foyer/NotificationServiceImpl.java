package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.NotificationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.NotificationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationFRepository notificationRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Supposant que c’est l’email
    }


}
