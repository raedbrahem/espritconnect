package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;

@Service
public class StripeServiceImpl {

    private final PaiementRepository paiementRepository;

    public StripeServiceImpl(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency, String description) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount * 100) // en centimes
                .setCurrency(currency)
                .setDescription(description)
                .build();

        return PaymentIntent.create(params);
    }
    @Transactional
    public void removePaiementById(Long idPaiement) {
        // Vérifier d'abord si le paiement existe
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID: " + idPaiement));

        // Supprimer le paiement
        paiementRepository.delete(paiement);

        // Optionnel: Mettre à jour la réservation associée si nécessaire
        // paiement.getReservation().setPaiement(null);
    }

}

