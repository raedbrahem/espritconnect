package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.AnswerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.NotificationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.QuestionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.VoteRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LearnITService implements ILearnITService {


@Autowired
    private QuestionRepository questionRepository;
@Autowired
     private AnswerRepository answerRepository;
@Autowired
     private VoteRepository voteRepository;
@Autowired
      private NotificationRepository notificationRepository;
@Autowired
private UserRepository userRepository;
@Autowired
    private EmailService emailService;

    //////Questions services/////
    @Override
    public List<Question> getAllQuestion() {
        return questionRepository.findAll();
    }

    @Override
    public Question GetQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question addQuestion(Question question) {
        // Récupérer l'utilisateur authentifié à partir du contexte de sécurité
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // L'email est utilisé comme identifiant unique

        // Récupérer l'utilisateur associé à la question à partir de la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Associer l'utilisateur à la question
        question.setUser(user);
        question.setCreatedAt(new Date());

        // Sauvegarder la question
        return questionRepository.save(question);
    }


    @Override
    public void removeQuestion(Long id) {
        questionRepository.deleteById(id);

    }

    @Override
    public Question modifyQuestion(Question question) {
        return questionRepository.save(question);
    }
    ///////////Answers services////////

    @Override
    public List<Answer> GetAllAnswer() {
        return answerRepository.findAll();
    }

    @Override
    public Answer GetAnswerById(Long id) {
        return answerRepository.findById(id).orElse(null);
    }

    @Override
    public Answer addAnswer(Answer answer, Long userId, Long questionId) {
        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Associer l'utilisateur et la question à la réponse
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setCreatedAt(new Date());

        // Sauvegarder la réponse
        Answer savedanswer =answerRepository.save(answer);
        // ✅ Ajouter une notification après l'ajout de la réponse
        createNewAnswerNotification(userId, questionId, savedanswer.getId());
        return savedanswer;
    }

    @Override
    public void removeAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    @Override
    public Answer modifyAnswer(Long id, Answer updatedAnswer) {
        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        existingAnswer.setContent(updatedAnswer.getContent());
        existingAnswer.setCreatedAt(updatedAnswer.getCreatedAt());

        return answerRepository.save(existingAnswer);
    }
///////////Votes services////////

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }

    public Vote addOrUpdateVote(Long userId, Long questionId, Long value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Optional<Vote> existingVote = voteRepository.findByUserAndQuestion(user, question);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            vote.setValue(Math.toIntExact(value));
            return voteRepository.save(vote);
        } else {
            Vote vote = new Vote();
            vote.setValue(Math.toIntExact(value));
            vote.setUser(user);
            vote.setQuestion(question);
            vote.setCreatedAt(new Date());
            return voteRepository.save(vote);
        }
    }

    // Récupérer le nombre de votes positifs pour une question
    public int getUpvotesForQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return voteRepository.countByQuestionAndValue(question, 1); // Votes positifs (value = 1)
    }

    // Récupérer le nombre de votes négatifs pour une question
    public int getDownvotesForQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return voteRepository.countByQuestionAndValueIs(question, -1); // Votes négatifs (value = -1)
    }

    // Récupérer le score total d'une question (upvotes - downvotes)
    public int getTotalScoreForQuestion(Long questionId) {
        int upvotes = getUpvotesForQuestion(questionId);
        int downvotes = getDownvotesForQuestion(questionId);
        return upvotes - downvotes;
    }
/////////Notifications services//////
    @Override
    public List<Notificationn> GetAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notificationn GetNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public Notificationn addNotification(Notificationn notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void removeNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public Notificationn modifyNotification(Notificationn notification) {
        return notificationRepository.save(notification);
    }
    public Notificationn createNewAnswerNotification(Long userId, Long questionId, Long answerId) {
        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Récupérer la réponse
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Créer la notification
        Notificationn notification = new Notificationn();
        notification.setContent("Une nouvelle réponse a été ajoutée à votre question : " + question.getTitle());
        notification.setType(NotificationType.NEW_ANSWER);
        notification.setUser(user);
        notification.setQuestion(question);
        notification.setAnswer(answer);
        notification.setCreatedAt(new Date());

        // Sauvegarder la notification
        Notificationn savedNotification = notificationRepository.save(notification);

        // ✅ Vérifier que `emailService` n'est pas `null`
        if (emailService != null) {
            String emailSubject = "Nouvelle réponse à votre question !";
            String emailContent = String.format(
                    "Bonjour %s,\n\nUne nouvelle réponse a été ajoutée à votre question : \"%s\".\n\n"
                            + "💬 Réponse : \"%s\"\n\nMerci d'utiliser notre plateforme !\n\nCordialement,\nL'équipe Support.",
                    user.getNom(), question.getTitle(), answer.getContent()
            );

            emailService.sendEmail(user.getEmail(), emailSubject, emailContent);
        } else {
            System.out.println("❌ Erreur : EmailService est NULL !");
        }

        return savedNotification;
    }

    public Notificationn createVoteNotification(Long userId, Long questionId) {
        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Créer la notification
        Notificationn notification = new Notificationn();
        notification.setContent("Un nouveau vote a été ajouté à votre question : " + question.getTitle());
        notification.setType(NotificationType.VOTE);
        notification.setUser(user);
        notification.setQuestion(question);
        notification.setCreatedAt(new Date());

        // Sauvegarder la notification
        return notificationRepository.save(notification);
    }
}

