package tn.esprit.examen.nomPrenomClasseExamen.config.Stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @PostConstruct
    public void init() {
        System.out.println("📌 Vérification : stripe.secret.key = " + stripeSecretKey);
        if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
            throw new IllegalStateException("🚨 Clé Stripe secrète non définie !");
        }
        Stripe.apiKey = stripeSecretKey;
    }
}
