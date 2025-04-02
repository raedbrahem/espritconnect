package tn.esprit.examen.nomPrenomClasseExamen.controllers.LearnIT;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Notificationn;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.ILearnITService;

@RestController
@AllArgsConstructor
public class NotificationRestController {

    @Autowired
    private ILearnITService notificationService;

    @PostMapping("/new-answer-notification")
    public ResponseEntity<Notificationn> createNewAnswerNotification(

            @RequestParam Long questionId,
            @RequestParam Long answerId) {

        Notificationn notification = notificationService.createNewAnswerNotification(questionId, answerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
    @PostMapping("/new-vote-notification")
    public ResponseEntity<Notificationn> createVoteNotification(
            @RequestParam Long userId,
            @RequestParam Long questionId) {

        Notificationn notification = notificationService.createVoteNotification(userId, questionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
}
