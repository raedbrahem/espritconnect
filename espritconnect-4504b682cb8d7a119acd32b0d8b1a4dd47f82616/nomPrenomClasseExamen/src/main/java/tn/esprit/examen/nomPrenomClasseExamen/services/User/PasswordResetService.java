package tn.esprit.examen.nomPrenomClasseExamen.services.User;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.PasswordResetToken;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.PasswordResetTokenRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    // Informations Twilio pour l'envoi de SMS
    @Value("${twilio.accountSid}")
    private String twilioAccountSid;
    @Value("${twilio.authToken}")
    private String twilioAuthToken;
    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    // Méthode pour l'envoi par email
    public void createPasswordResetTokenForEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        Date expiryDate = getExpiryDate();

        PasswordResetToken resetToken = getOrCreateToken(user, token, expiryDate);
        tokenRepository.save(resetToken);
        sendResetEmail(user.getEmail(), token);
    }

    // Méthode pour l'envoi par SMS
    public void createPasswordResetTokenForSms(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        Date expiryDate = getExpiryDate();

        PasswordResetToken resetToken = getOrCreateToken(user, token, expiryDate);
        tokenRepository.save(resetToken);
        sendResetSms(user.getTelephone(), token);
    }

    // Méthode commune pour obtenir ou créer le token
    private PasswordResetToken getOrCreateToken(User user, String token, Date expiryDate) {
        Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByUser(user);
        PasswordResetToken resetToken;
        if (existingTokenOpt.isPresent()) {
            resetToken = existingTokenOpt.get();
            resetToken.setToken(token);
            resetToken.setExpiryDate(expiryDate);
        } else {
            resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(token);
            resetToken.setExpiryDate(expiryDate);
        }
        return resetToken;
    }

    // Méthode utilitaire pour calculer la date d'expiration (par exemple 24 heures)
    private Date getExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        return cal.getTime();
    }

    // Envoi de l'email de réinitialisation
    private void sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant :\n" + resetUrl);
        message.setFrom(fromEmail);
        mailSender.send(message);
    }

    // Envoi du SMS de réinitialisation via Twilio
    private void sendResetSms(String phoneNumber, String token) {
        Twilio.init(twilioAccountSid, twilioAuthToken);
        String resetUrl = "http://localhost:8089/api/reset-password?token=" + token;
        String smsBody = "Pour réinitialiser votre mot de passe, cliquez sur: " + resetUrl;
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                smsBody
        ).create();
        System.out.println("SMS envoyé: " + message.getSid());
    }

    // Réinitialisation du mot de passe (inchangée)
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}
