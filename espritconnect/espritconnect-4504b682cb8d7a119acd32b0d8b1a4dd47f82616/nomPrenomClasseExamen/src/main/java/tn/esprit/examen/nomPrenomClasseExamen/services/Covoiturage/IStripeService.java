package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

public interface IStripeService {
    public String createPaymentIntent(Long amount, String currency, String description) throws Exception;
}
