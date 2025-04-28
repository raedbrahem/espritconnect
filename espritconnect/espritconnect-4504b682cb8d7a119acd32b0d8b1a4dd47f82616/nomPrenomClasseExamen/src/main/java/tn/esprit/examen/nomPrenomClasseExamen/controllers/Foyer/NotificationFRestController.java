package tn.esprit.examen.nomPrenomClasseExamen.controllers.Foyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.NotificationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.IFoyerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.NotificationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.Foyer.NotificationFEmitterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notificationsF")
public class NotificationFRestController {

    @Autowired
    private NotificationFEmitterService emitterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IFoyerRepository foyerRepository;

    @Autowired
    private NotificationFRepository notificationFRepository;



    @GetMapping("/by-email/{email}")
    public ResponseEntity<List<NotificationF>> getAll(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            List<NotificationF> notifications = notificationFRepository.findByUserIdOrderByCreatedAtDesc(user.get().getId());
            return ResponseEntity.ok(notifications);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/mark-all-as-read/{email}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            List<NotificationF> notifs = notificationFRepository.findByUser_IdAndLuFalse(user.get().getId());
            notifs.forEach(n -> n.setLu(true));
            notificationFRepository.saveAll(notifs);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping(value = "/stream/by-email/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable String email) {
        System.out.println("🟢 Connexion SSE pour : " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return emitterService.createEmitter(user.getId());
    }

    // 🔁 Pour tester manuellement une notification
    @PostMapping("/test-sse")
    public ResponseEntity<String> testSse(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        NotificationF testNotif = new NotificationF();
        testNotif.setMessage("Notification test envoyée ✔");
        testNotif.setUser(user);
        testNotif.setFoyer(null); // ✅ autorisé car nullable
        testNotif.setLu(false);

        // 🔐 Sauvegarder dans la base AVANT l'envoi
        NotificationF savedNotif = notificationFRepository.save(testNotif);

        // 🔔 Ensuite envoyer via SSE
        emitterService.sendNotification(user.getId(), savedNotif);

        return ResponseEntity.ok("Notification test envoyée avec succès !");
    }


}   