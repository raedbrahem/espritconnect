package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.springframework.stereotype.Service;

@Service
public class AiCategoryService {

    public String predictCategory(String imagePath) {
        try {
            String basePath = new File("").getAbsolutePath(); // root of project
            File scriptDir = new File("C:\\Users\\Tifa\\Desktop\\Master pull Spring\\espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616\\nomPrenomClasseExamen", "ai_auto_categorizer");

            ProcessBuilder pb = new ProcessBuilder("python", "enhanced_categorizer.py", imagePath);
            pb.directory(scriptDir);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String prediction = reader.readLine();

            int exitCode = process.waitFor();
            if (exitCode == 0 && prediction != null && !prediction.trim().equalsIgnoreCase("UNKNOWN")) {
                return prediction.trim().toUpperCase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}