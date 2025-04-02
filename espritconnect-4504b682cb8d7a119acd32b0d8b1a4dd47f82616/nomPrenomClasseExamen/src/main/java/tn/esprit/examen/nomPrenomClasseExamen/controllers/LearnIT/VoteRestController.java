package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Vote;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

@RestController
@AllArgsConstructor
@RequestMapping("/vote")
public class VoteRestController {
    @Autowired
    ILearnITService learnITService;

    // Ajouter ou mettre à jour un vote
    @PostMapping("/user/{userId}/question/{questionId}")
    public Vote addOrUpdateVote(
            @PathVariable Long userId,
            @PathVariable Long questionId,
            @RequestParam Long value) { // 1 pour upvote, -1 pour downvote
        return learnITService.addOrUpdateVote(userId, questionId, value);
    }

    @GetMapping("/question/{questionId}/upvotes")
    public int getUpvotesForQuestion(@PathVariable Long questionId) {
        return learnITService.getUpvotesForQuestion(questionId);
    }

    @GetMapping("/question/{questionId}/downvotes")
    public int getDownvotesForQuestion(@PathVariable Long questionId) {
        return learnITService.getDownvotesForQuestion(questionId);
    }
    @GetMapping("/question/{questionId}/score")
    public int getTotalScoreForQuestion(@PathVariable Long questionId) {
        return learnITService.getTotalScoreForQuestion(questionId);
    }
}
