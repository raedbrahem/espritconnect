package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;

import java.io.IOException;

@Service
public class CohereService {

    @Value("${cohere.api.key}")
    private String apiKey;

    public String generateFoyerDescription(Foyer foyer) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = String.format(
                "Génère une description engageante et attrayante pour une annonce immobilière à partir des informations suivantes :\n\n" +
                        "Ces foyers sont tous à louer, exclusivement destinés aux étudiants d'Esprit.\n" +
                        "La description peut être rédigée en français.\n\n" + // Ajout de la précision sur la langue
                        "Description : %s\n" +
                        "Localisation : %s\n" +
                        "Superficie : %.1f m²\n" +
                        "Nombre de chambres : %d\n" +
                        "Meublé : %s\n" +
                        "Prix : %.0f TND\n\n" +
                        "Description générée :",
                foyer.getDescription(),
                foyer.getLocalisation(),
                foyer.getSuperficie(),
                foyer.getNbrDeChambre(),
                foyer.getMeuble() ? "Oui" : "Non",
                foyer.getPrix()
        );


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "command");
        jsonObject.put("prompt", prompt);
        jsonObject.put("max_tokens", 100); // tu peux ajuster selon la longueur souhaitée
        jsonObject.put("temperature", 0.7); // pour un peu de créativité mais pas trop

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://api.cohere.ai/v1/generate")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur Cohere: " + response.body().string());
            }

            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);
            return responseJson.getJSONArray("generations").getJSONObject(0).getString("text").trim();
        }
    }
}
