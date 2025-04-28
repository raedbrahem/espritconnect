package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CohereServiceFoued {

    @Value("${cohere.api.key}")
    private String apiKey;

    public String generateQuestion(String context) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = "Génère une question sur ce contenu:\n\n\"" + context + "\"\n\nQuestion:";
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "command");
        jsonObject.put("prompt", prompt);
        jsonObject.put("max_tokens", 60);
        jsonObject.put("temperature", 0.8);

        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://api.cohere.ai/v1/generate")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        System.out.println("API Key = " + apiKey);


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur Cohere: " + response.body().string());
            }

            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);
            return responseJson.getJSONArray("generations").getJSONObject(0).getString("text").trim();

        }

    }
    public String translateText(String input, String targetLanguage) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Construire le prompt pour la traduction
        String prompt = "Traduire ce texte en " + targetLanguage + ":\n\n\"" + input + "\"\n\nTraduction:";

        // Spécifier les paramètres de la requête
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "command");  // Utilise le modèle que tu as choisi
        jsonObject.put("prompt", prompt);
        jsonObject.put("max_tokens", 200);  // Ajuste ce paramètre en fonction de la longueur du texte
        jsonObject.put("temperature", 0.7);  // Utilisation de la température pour plus de créativité

        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);

        // Préparer la requête HTTP
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

            // Extraire la réponse de l'API Cohere
            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);
            return responseJson.getJSONArray("generations").getJSONObject(0).getString("text").trim();
        }
    }

    public String summarizeToShorterForm(String content) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Modification du prompt pour demander un résumé plus complet mais toujours court
        String prompt = "Fais un résumé concis et clair du texte suivant :\n\n\"" + content + "\"\n\nRésumé concis :";

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "command");
        jsonObject.put("prompt", prompt);
        jsonObject.put("max_tokens", 100); // Augmenter la limite de tokens à 100 pour plus de contenu
        jsonObject.put("temperature", 0.3); // Une température légèrement plus élevée pour plus de flexibilité

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
            String result = responseJson.getJSONArray("generations").getJSONObject(0).getString("text").trim();

            // Supprimer la troncation des 7 mots et retourner le résumé complet
            return result;
        }
    }


    public String chatWithCohere(String userMessage) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = "Tu es un assistant utile et intelligent. Réponds de manière claire et pertinente à la question suivante :\n\n"
                + "Utilisateur : " + userMessage + "\nAssistant :";

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "command");
        jsonObject.put("prompt", prompt);
        jsonObject.put("max_tokens", 100); // Longueur de la réponse
        jsonObject.put("temperature", 0.7); // Un peu de créativité

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
