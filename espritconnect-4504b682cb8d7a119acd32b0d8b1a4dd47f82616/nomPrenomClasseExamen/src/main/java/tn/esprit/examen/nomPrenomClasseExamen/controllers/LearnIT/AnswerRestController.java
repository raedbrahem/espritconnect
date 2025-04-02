package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerRestController {
    @Autowired
    ILearnITService learnITService;

    // Créer une réponse
    @PostMapping("/question/{questionId}/answer")
    public Answer addAnswer(@RequestBody Answer answer, @PathVariable Long questionId) {
        return learnITService.addAnswer(answer, questionId);
    }

    @PutMapping("/{id}")
    public Answer updateAnswer(@PathVariable Long id, @RequestBody Answer updatedAnswer) {
        Answer answer = learnITService.modifyAnswer(id, updatedAnswer);
        return answer;
    }
    @GetMapping("/Get-all-Answers")
    public List<Answer> getAnswers() {
        List<Answer> listAnswers = learnITService.GetAllAnswer();
        return listAnswers;
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
