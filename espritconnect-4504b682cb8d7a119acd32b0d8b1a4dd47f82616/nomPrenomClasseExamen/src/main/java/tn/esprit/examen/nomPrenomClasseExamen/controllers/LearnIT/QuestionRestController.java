package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Tag;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

import java.util.List;

@RestController
@RequestMapping("/question")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionRestController {
    @Autowired
    ILearnITService learnITService;



    @GetMapping("/Get-all-Questions")
    public ResponseEntity<List<Question>> getQuestions() {
        return ResponseEntity.ok(learnITService.getAllQuestion());
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
    @GetMapping({"/tag/{tag}"})
    public List<Question> getQuestionsByTag(@PathVariable Tag tag) {
        return this.learnITService.getQuestionsByTag(tag);
    }
    ////////////////////////////Answers services////////////////////////
}
