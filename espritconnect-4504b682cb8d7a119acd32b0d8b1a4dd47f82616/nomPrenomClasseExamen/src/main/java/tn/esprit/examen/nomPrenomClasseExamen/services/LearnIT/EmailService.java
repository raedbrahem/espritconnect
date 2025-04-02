package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotificationEmail(String recipientEmail, String message) {
        sendEmail(recipientEmail, "Nouvelle réponse à votre question", message);
    }

    public void sendEmail(String email, String emailSubject, String emailContent) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(emailContent);

        mailSender.send(mailMessage);
    }
}
