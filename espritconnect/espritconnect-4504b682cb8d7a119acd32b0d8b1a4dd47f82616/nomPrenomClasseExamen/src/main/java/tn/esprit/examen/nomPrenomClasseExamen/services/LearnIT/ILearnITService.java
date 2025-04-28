package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Tag;

import java.util.List;

public interface ILearnITService {
    //////Questions services/////
    List<Question> getAllQuestion();
    Question GetQuestionById(Long id);

    Question addQuestion(Question question);
    void removeQuestion(Long id);
    Question modifyQuestion(Question question);
    public List<Question> getQuestionsByTag(Tag tag) ;

    public void reportQuestion(Long id, String reason) ;
    public List<Question> getReportedQuestions() ;



    ///////////Answers services////////
    List<Answer> GetAllAnswer();
    Answer GetAnswerById(Long id);
    Answer addAnswer(Answer answer, Long questionId);
    void removeAnswer(Long id);
    Answer modifyAnswer(Long id, Answer updatedAnswer);
    public Question downvote(Long id);
    public Question upvote(Long id);
}
