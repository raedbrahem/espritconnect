package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MiroService {

    @Value("${miro.token}")
    private String miroToken; // Miro token from application.properties

    @Value("${miro.dashboard.id}")
    private String dashboardId; // Miro dashboard ID from application.properties

    private final RestTemplate restTemplate;

    public MiroService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getDashboardDetails() {
        String url = "https://api.miro.com/v1/boards/" + dashboardId;

        // Create the HTTP header with the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + miroToken);

        // Create an HttpEntity object with the header
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Send the GET request and retrieve the response
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Return the response body
        return response.getBody();
    }
}