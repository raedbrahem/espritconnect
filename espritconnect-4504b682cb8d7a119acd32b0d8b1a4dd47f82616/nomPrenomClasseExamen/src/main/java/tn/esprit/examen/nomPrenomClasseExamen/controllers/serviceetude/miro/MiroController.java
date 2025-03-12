package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude.miro;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.services.miro.MiroService;

@RestController
public class MiroController {

    private final MiroService miroService;

    public MiroController(MiroService miroService) {
        this.miroService = miroService;
    }

    @GetMapping("/miro/dashboard")
    public String getDashboardDetails() {
        return miroService.getDashboardDetails();
    }
}
