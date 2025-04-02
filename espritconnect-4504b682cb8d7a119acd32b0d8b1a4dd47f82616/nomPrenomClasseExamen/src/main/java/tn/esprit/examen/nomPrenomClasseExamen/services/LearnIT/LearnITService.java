package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

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

    @Override

        public List<Question> getQuestionsByTag(Tag tag) {
            return this.questionRepository.findByTag(tag);
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
    public Answer addAnswer(Answer answer, Long questionId) {
        // 🔹 Récupérer l'utilisateur authentifié à partir du contexte de sécurité
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // Utilisation de l'email comme identifiant unique

        // 🔹 Récupérer l'utilisateur depuis la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Récupérer la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // 🔹 Associer l'utilisateur et la question à la réponse
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setCreatedAt(new Date());

        // 🔹 Sauvegarder la réponse
        Answer savedAnswer = answerRepository.save(answer);

        // ✅ Ajouter une notification après l'ajout de la réponse
        createNewAnswerNotification( questionId, savedAnswer.getId());

        return savedAnswer;
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




    @Transactional
    public Vote addOrUpdateVote(Long questionId, Long value) {
        // 1. Récupération de l'utilisateur depuis le contexte de sécurité
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 2. Recherche de l'utilisateur par email (ou autre identifiant selon votre implémentation)
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Récupération de la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // 4. Recherche du vote existant
        Optional<Vote> existingVote = voteRepository.findByUserAndQuestion(user, question);

        if (existingVote.isPresent()) {
            // Mise à jour du vote existant
            Vote vote = existingVote.get();
            vote.setValue(Math.toIntExact(value));
            vote.setCreatedAt(new Date()); // Ajout de la date de mise à jour
            return voteRepository.save(vote);
        } else {
            // Création d'un nouveau vote
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
    public Notificationn createNewAnswerNotification(Long questionId, Long answerId) {
        // 🔹 Récupérer la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // 🔹 Récupérer l'utilisateur qui a posé la question
        User user = question.getUser();

        // 🔹 Récupérer la réponse
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // 🔹 Créer la notification
        Notificationn notification = new Notificationn();
        notification.setContent("Une nouvelle réponse a été ajoutée à votre question : " + question.getTitle());
        notification.setType(NotificationType.NEW_ANSWER);
        notification.setUser(user);
        notification.setQuestion(question);
        notification.setAnswer(answer);
        notification.setCreatedAt(new Date());

        // 🔹 Sauvegarder la notification
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

