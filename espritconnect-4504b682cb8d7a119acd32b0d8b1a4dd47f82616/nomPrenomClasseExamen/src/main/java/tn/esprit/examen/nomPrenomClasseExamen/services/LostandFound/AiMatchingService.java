package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class AiMatchingService {
    public void runMatcher(String category) {
        try {
            // You can pass image path as third argument if needed
            ProcessBuilder pb = new ProcessBuilder("python", "match.py", category);
            pb.directory(new File("path/to/your/hybrid_matcher")); // adjust this path

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line); // or store/log this
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
