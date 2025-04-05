// src/app/Services/StorageService.java
package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class StorageService {

    // Dossier où les fichiers seront sauvegardés
    private final String uploadDir = "uploads";

    public String storeFile(MultipartFile file) {
        // Créer le dossier si nécessaire
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Générer un nom unique pour éviter les collisions
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), Paths.get(uploadDir).resolve(fileName));
            // Retourne le chemin du fichier (à adapter pour générer une URL absolue si besoin)
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }
}
