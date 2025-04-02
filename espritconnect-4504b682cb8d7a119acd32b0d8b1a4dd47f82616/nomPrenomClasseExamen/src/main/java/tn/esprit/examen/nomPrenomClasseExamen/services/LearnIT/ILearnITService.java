package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.*;

import java.util.List;

public interface ILearnITService {
    //////Questions services/////
    List<Question> getAllQuestion();
    Question GetQuestionById(Long id);
    List<Question> getQuestionsByTag(Tag tag);
    Question addQuestion(Question question);
    void removeQuestion(Long id);
    Question modifyQuestion(Question question);
    ///////////Answers services////////
    List<Answer> GetAllAnswer();
    Answer GetAnswerById(Long id);
    Answer addAnswer(Answer answer, Long questionId);
    void removeAnswer(Long id);
    Answer modifyAnswer(Long id, Answer updatedAnswer);
    ///////////Votes services////////

    void removeVote(Long id);
    public Vote addOrUpdateVote( Long questionId, Long value);
    public int getUpvotesForQuestion(Long questionId);
    public int getDownvotesForQuestion(Long questionId);
    ///////Notifications services//////
    List<Notificationn> GetAllNotifications();
    Notificationn GetNotificationById(Long id);
    Notificationn addNotification(Notificationn notification);
    void removeNotification(Long id);
    Notificationn modifyNotification(Notificationn notification);

    int getTotalScoreForQuestion(Long questionId);

    public Notificationn createVoteNotification(Long userId, Long questionId);
    public Notificationn createNewAnswerNotification(Long questionId, Long answerId);
}
