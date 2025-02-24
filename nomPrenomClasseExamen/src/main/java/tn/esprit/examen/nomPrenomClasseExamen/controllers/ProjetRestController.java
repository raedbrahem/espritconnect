package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ProjetDetail;
import tn.esprit.examen.nomPrenomClasseExamen.services.IServiceProjetDetail;

@RequiredArgsConstructor
@RequestMapping("examen")
@RestController
public class ProjetRestController {
    @Autowired
    private IServiceProjetDetail services;
    @PostMapping("/add-projetdetail")
    public ProjetDetail add(@RequestBody ProjetDetail projetDetail){
        return  services.add(projetDetail);
    }

}
