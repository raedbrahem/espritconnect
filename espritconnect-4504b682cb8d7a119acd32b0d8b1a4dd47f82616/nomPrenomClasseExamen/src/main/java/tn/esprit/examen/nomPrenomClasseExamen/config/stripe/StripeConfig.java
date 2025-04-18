package tn.esprit.examen.nomPrenomClasseExamen.config.stripe;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key.secret}")
    private String stripeSecretKey;

    @Bean
    public String stripeApiKey() {
        Stripe.apiKey = stripeSecretKey;
        return stripeSecretKey;
    }
}
