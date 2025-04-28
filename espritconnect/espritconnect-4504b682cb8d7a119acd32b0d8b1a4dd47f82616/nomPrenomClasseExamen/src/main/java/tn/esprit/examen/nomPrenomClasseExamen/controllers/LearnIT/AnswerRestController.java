package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.IAEvaluationResult;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.AnswerEvaluationService;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerRestController {
    @Autowired
    ILearnITService learnITService;
    @Autowired
    AnswerEvaluationService evaluationService;


    // Créer une réponse
    @PostMapping("/question/{questionId}")
    public Answer addAnswer(@RequestBody Answer answer, @PathVariable Long questionId) {
        Question question = learnITService.GetQuestionById(questionId);

        // Appel IA Cohere pour scorer la réponse
        IAEvaluationResult eval = evaluationService.evaluateAnswer(
                question.getContent(),
                answer.getContent()
        );

        // Injection du score et commentaire IA
        answer.setScoreIA(eval.getScoreIA());
        answer.setCommentaireIA(eval.getCommentaireIA());

        return learnITService.addAnswer(answer, questionId);
    }

    @PutMapping("/{id}")
    public Answer updateAnswer(@PathVariable Long id, @RequestBody Answer updatedAnswer) {
        Answer answer = learnITService.modifyAnswer(id, updatedAnswer);
        return answer;
    }
    @GetMapping("/Get-all-Answers")
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = learnITService.GetAllAnswer();
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/Get-Answer/{Answer-id}")
    public Answer GetAnswer(@PathVariable("Answer-id") Long AnswerID) {
        Answer Answer = learnITService.GetAnswerById(AnswerID);
        return Answer;
    }
    @DeleteMapping("/remove-Answer/{answer-id}")
    public void removeAnswer(@PathVariable("answer-id") Long answerID) {
        learnITService.removeAnswer(answerID);
    }


}
