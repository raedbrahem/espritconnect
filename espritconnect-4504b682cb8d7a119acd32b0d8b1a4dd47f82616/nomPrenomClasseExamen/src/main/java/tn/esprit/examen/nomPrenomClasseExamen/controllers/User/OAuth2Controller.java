package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @GetMapping("/me")
    public Map<String, Object> getOAuth2User(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> response = new HashMap<>();
        response.put("email", oauth2User.getAttribute("email"));
        response.put("name", oauth2User.getAttribute("name"));
        response.put("picture", oauth2User.getAttribute("picture"));
        return response;
    }
}

