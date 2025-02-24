package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Projet;
import tn.esprit.examen.nomPrenomClasseExamen.services.IServices;



@RequiredArgsConstructor
@RequestMapping("examen")
@RestController
public class ProjetDetailRestController {
    @Autowired
    private IServices services;
    @PostMapping("/add-projet")
    public Projet add(@RequestBody Projet projet){
        return  services.add(projet);
    }
}
