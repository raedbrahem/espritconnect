package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude.miro;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.services.miro.MiroService;

import java.net.URI;

@RestController
public class MiroController {

    private final MiroService miroService;

    public MiroController(MiroService miroService) {
        this.miroService = miroService;
    }

    @GetMapping("/miro/dashboard")
    public ResponseEntity<Void> getDashboardDetails() {
        String viewLink = miroService.getDashboardDetails().getViewLink();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(viewLink));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}