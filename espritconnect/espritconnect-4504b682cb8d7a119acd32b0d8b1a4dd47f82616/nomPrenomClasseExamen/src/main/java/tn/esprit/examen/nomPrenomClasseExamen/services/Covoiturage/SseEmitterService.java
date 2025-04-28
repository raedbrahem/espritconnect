package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Notification;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // Timeout 30min
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            System.out.println("✅ [SSE] Émetteur terminé pour l'utilisateur ID : " + userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            System.out.println("⏳ [SSE] Timeout de l'émetteur pour l'utilisateur ID : " + userId);
        });

        return emitter;
    }

    public void sendNotification(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notification, MediaType.APPLICATION_JSON));
                System.out.println("📤 [SSE] Notification envoyée à l'utilisateur ID : " + userId);
            } catch (IOException e) {
                emitters.remove(userId);
                System.out.println("❌ [SSE] Erreur d'envoi, suppression de l'émetteur pour ID : " + userId);
            }
        } else {
            System.out.println("⚠️ [SSE] Aucun émetteur trouvé pour ID : " + userId);
        }
    }
}
