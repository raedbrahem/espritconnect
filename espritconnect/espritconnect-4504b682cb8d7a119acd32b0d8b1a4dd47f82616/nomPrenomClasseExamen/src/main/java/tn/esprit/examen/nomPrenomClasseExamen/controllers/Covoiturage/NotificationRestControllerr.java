    package tn.esprit.examen.nomPrenomClasseExamen.controllers.Covoiturage;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
    import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Notification;
    import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
    import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.NotificationRepositoryy;
    import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
    import tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage.SseEmitterService;

    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/notifications")
    public class NotificationRestControllerr {

        private final SseEmitterService sseService;
        private final UserRepository userRepository;
        private final NotificationRepositoryy notificationRepositoryy;

        @Autowired
        public NotificationRestControllerr(SseEmitterService sseService, UserRepository userRepository, NotificationRepositoryy notificationRepositoryy) {
            this.sseService = sseService;
            this.userRepository = userRepository;
            this.notificationRepositoryy = notificationRepositoryy;
        }

        @GetMapping(path = "/stream/by-email/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public SseEmitter streamNotificationsByEmail(@PathVariable String email) {
            System.out.println("🔔 [SSE] Connexion ouverte pour l'email : " + email);

            return userRepository.findByEmail(email)
                    .map(user -> {
                        Long userId = user.getId();
                        System.out.println("✅ [SSE] Emetteur créé pour l'utilisateur ID : " + userId);
                        return sseService.createEmitter(userId);
                    })
                    .orElseThrow(() -> {
                        System.out.println("❌ [SSE] Utilisateur non trouvé pour l'email : " + email);
                        return new RuntimeException("Utilisateur introuvable avec l'email : " + email);
                    });
        }

        @GetMapping("/by-email/{email}")
        public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable String email) {
            System.out.println("📩 [GET] Demande de notifications pour l'email : " + email);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                Long userId = user.get().getId();
                List<Notification> notifs = notificationRepositoryy.findByUser_IdOrderByDateCreationDesc(userId);
                System.out.println("✅ [GET] Notifications récupérées pour l'utilisateur ID : " + userId + ", total : " + notifs.size());
                return ResponseEntity.ok(notifs);
            }

            System.out.println("❌ [GET] Utilisateur non trouvé pour l'email : " + email);
            return ResponseEntity.notFound().build();
        }

        @PutMapping("/mark-all-as-read/{email}")
        public ResponseEntity<Void> markAllAsRead(@PathVariable String email) {
            System.out.println("📨 [PUT] Marquer toutes les notifications comme lues pour : " + email);

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                List<Notification> notifs = notificationRepositoryy.findByUser_IdAndLueFalse(user.get().getId());
                notifs.forEach(n -> n.setLue(true));
                notificationRepositoryy.saveAll(notifs);
                System.out.println("✅ [PUT] " + notifs.size() + " notifications marquées comme lues.");
                return ResponseEntity.ok().build();
            }

            System.out.println("❌ [PUT] Utilisateur non trouvé pour l'email : " + email);
            return ResponseEntity.notFound().build();
        }
    }
