package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Tag;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.CohereServiceFoued;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question")
@AllArgsConstructor
public class QuestionRestController {
    @Autowired
    ILearnITService learnITService;
    private final CohereServiceFoued cohereService;




    @GetMapping("/Get-all-Questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = learnITService.getAllQuestion();

        return ResponseEntity.ok(questions);
    }



    @GetMapping("/Get-Question/{Question-id}")
    public ResponseEntity GetQuestion(@PathVariable("Question-id") Long questionID) {
        Question Question = learnITService.GetQuestionById(questionID);
        return ResponseEntity.ok(Question);
    }
    @PostMapping("/add-Question")
    public Question addQuestion(@RequestBody Question question) {
        return learnITService.addQuestion(question);
    }
    @DeleteMapping("/remove-Question/{Question-id}")
    public void removeQuestion(@PathVariable("Question-id") Long questionID) {
        learnITService.removeQuestion(questionID);
    }

    @PutMapping("/modify-Question")
    public Question modifyQuestion(@RequestBody Question q) {
        Question Question = learnITService.modifyQuestion(q);
        return Question;
    }

    @PostMapping("/upvote/{question-id}")
    public ResponseEntity<Question> upvoteQuestion(@PathVariable("question-id") Long questionId) {
        Question question = learnITService.upvote(questionId);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/downvote/{question-id}")
    public ResponseEntity<Question> downvoteQuestion(@PathVariable("question-id") Long questionId) {
        Question question = learnITService.downvote(questionId);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/generate")
    public ResponseEntity<String> generateQuestion(@RequestBody Map<String, String> request) {
        String context = request.get("content");
        try {
            String question = cohereService.generateQuestion(context);
            return ResponseEntity.ok(question);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur Cohere: " + e.getMessage());
        }
    }

    @GetMapping("/by-tag")
    public ResponseEntity<List<Question>> getQuestionsByTag(@RequestParam Tag tag) {
        return ResponseEntity.ok(learnITService.getQuestionsByTag(tag));
    }

    @GetMapping("/translate/{question-id}")
    public ResponseEntity<String> translateQuestion(
            @PathVariable("question-id") Long questionId,
            @RequestParam String language) {
        Question question = learnITService.GetQuestionById(questionId);
        if (question == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            // Utilise la méthode de traduction réelle
            String translated = cohereService.translateText(question.getContent(), language);
            return ResponseEntity.ok(translated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur de traduction : " + e.getMessage());
        }
    }
    @PostMapping("/report/{id}")
    public ResponseEntity<Map<String, String>> reportQuestion(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        learnITService.reportQuestion(id, reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "La question a été signalée.");

        return ResponseEntity.ok(response); // <-- maintenant Angular recevra un vrai JSON
    }


    @GetMapping("/reported")
    public List<Question> getReportedQuestions() {
        return learnITService.getReportedQuestions();
    }


    @GetMapping("/summarize/{questionId}")
    public ResponseEntity<String> summarize(@PathVariable Long questionId) {
        Question q =learnITService.GetQuestionById(questionId);
        try {
            String summary = cohereService.summarizeToShorterForm(q.getContent());
            return ResponseEntity.ok(summary);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du résumé");
        }
    }


    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            String response = cohereService.chatWithCohere(userMessage);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }


}
