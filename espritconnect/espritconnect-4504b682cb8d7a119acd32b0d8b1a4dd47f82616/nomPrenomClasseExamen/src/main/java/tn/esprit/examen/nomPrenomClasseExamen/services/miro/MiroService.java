package tn.esprit.examen.nomPrenomClasseExamen.services.miro;

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
    private String miroToken;

    @Value("${miro.dashboard.id}")
    private String dashboardId;

    private final RestTemplate restTemplate;

    public MiroService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DashboardDetails getDashboardDetails() {
        String url = "https://api.miro.com/v1/boards/" + dashboardId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + miroToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Parse the JSON response into a DashboardDetails object
        ResponseEntity<DashboardDetails> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, DashboardDetails.class);
        return response.getBody();
    }
}