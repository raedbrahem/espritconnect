package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        // Forward to index.html so Angular can handle the routing
        return "forward:/index.html";
    }
}