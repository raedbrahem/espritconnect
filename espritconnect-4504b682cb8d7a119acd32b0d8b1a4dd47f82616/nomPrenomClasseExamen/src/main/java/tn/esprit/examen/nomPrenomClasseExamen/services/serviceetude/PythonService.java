package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

// src/main/java/com/yourpackage/service/PythonService.java

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class PythonService {
    // In your PythonService.java
    public List<String> runSpeechToText() {
        List<String> outputLines = new ArrayList<>();
        try {
            String pythonScriptPath = "\"C:\\Users\\brahe\\Desktop\\back\\espritconnect-4504b682cb8d7a119acd32b0d8b1a4dd47f82616\\nomPrenomClasseExamen\\src\\main\\python\\example\\test_microphone.py\"";
            System.out.println("Attempting to run: " + pythonScriptPath); // Debug log
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Print process output in real-time
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Python Output: " + line); // Debug log
                    outputLines.add(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputLines;
    }
}