package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.IAEvaluationResult;

import java.io.IOException;

@Service
public class AnswerEvaluationService {

    @Value("${cohere.api.key}")
    private String apiKey;

    public IAEvaluationResult evaluateAnswer(String question, String answer) {
        OkHttpClient client = new OkHttpClient();

        String prompt = String.format(
                "Voici une question d'examen et une réponse d'étudiant.\n" +
                        "Donne une évaluation JSON comme ceci : {\"scoreIA\": 0-100, \"commentaireIA\": \"...\"}\n" +
                        "Question : %s\nRéponse : %s", question, answer);

        // Bien importer okhttp3.MediaType !
        MediaType mediaType = MediaType.get("application/json");

        JSONObject json = new JSONObject();
        json.put("model", "command-r-plus");
        json.put("message", prompt);
        json.put("temperature", 0.3);

        RequestBody body = RequestBody.create(json.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://api.cohere.ai/v1/chat")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new IAEvaluationResult(0, "Erreur réseau avec Cohere");
            }

            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);

            // ⚠️ à adapter selon structure réelle de la réponse
            String content = responseJson.optString("text"); // ou dans un tableau, selon réponse de l'API

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, IAEvaluationResult.class);

        } catch (IOException e) {
            return new IAEvaluationResult(0, "Erreur de communication avec l'IA");
        }
    }
}
