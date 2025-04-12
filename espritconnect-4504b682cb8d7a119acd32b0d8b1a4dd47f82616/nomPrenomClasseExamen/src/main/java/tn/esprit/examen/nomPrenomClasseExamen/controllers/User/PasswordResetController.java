package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.PasswordResetService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Endpoint pour déclencher l'envoi du lien ou du SMS de réinitialisation
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String channel = request.get("channel"); // "email" ou "sms"

        String message;
        if ("sms".equalsIgnoreCase(channel)) {
            passwordResetService.createPasswordResetTokenForSms(email);
            message = "Un lien de réinitialisation a été envoyé par SMS.";
        } else {
            passwordResetService.createPasswordResetTokenForEmail(email);
            message = "Un lien de réinitialisation a été envoyé à votre email.";
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }


    // Endpoint pour réinitialiser le mot de passe
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Le mot de passe a été réinitialisé avec succès.");
    }

}
