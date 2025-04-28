package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class EmailServicee {

    private final JavaMailSender javaMailSender;

    public void sendQrCodeEmail(String to, String subject, String text, BufferedImage qrCodeImage) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        // Encapsuler proprement ton contenu HTML
        String htmlContent = "<html><body>" +
                "<h2>Confirmation de votre réservation</h2>" +
                "<p>" + text + "</p>" +
                "<p>Scannez ce QR Code pour accéder à votre trajet :</p>" +
                "<img src='cid:qrcode'>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        // Convertir ton QR code BufferedImage en pièce jointe inline
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", bos);
        helper.addInline("qrcode", new jakarta.mail.util.ByteArrayDataSource(bos.toByteArray(), "image/png"));

        javaMailSender.send(message);
    }
}
