package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

// src/main/java/com/yourpackage/controller/SpeechController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.PythonService;

import java.util.List;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Autowired
    private PythonService pythonService;

    @GetMapping("/transcribe")
    public List<String> transcribeSpeech() {
        return pythonService.runSpeechToText();
    }
}