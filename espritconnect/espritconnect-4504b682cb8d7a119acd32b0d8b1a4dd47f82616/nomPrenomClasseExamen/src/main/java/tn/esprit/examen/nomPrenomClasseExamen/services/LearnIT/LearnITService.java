package tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Tag;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.AnswerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT.QuestionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class LearnITService implements ILearnITService {


@Autowired
    private QuestionRepository questionRepository;
@Autowired
     private AnswerRepository answerRepository;


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
    public Question modifyQuestion(Question updatedQuestion) {
        // Récupérer la question existante à partir de l'ID
        Question existingQuestion = questionRepository.findById(updatedQuestion.getId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Ne PAS modifier l'utilisateur d'origine ni la date de création
        // existingQuestion.setUser(...) -> on garde le user existant
        // existingQuestion.setCreatedAt(...) -> on garde l'ancienne date

        // Modifier uniquement le contenu de la question
        existingQuestion.setContent(updatedQuestion.getContent());

        // Mettre à jour la date de modification
        existingQuestion.setUpdatedAt(new Date());

        return questionRepository.save(existingQuestion);}

    @Override
    public List<Question> getQuestionsByTag(Tag tag) {
        return questionRepository.findByTag(tag);
    }

    @Override
    public void reportQuestion(Long id, String reason) {

            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            question.setReported(true);
            question.setReportReason(reason);

            questionRepository.save(question);
        }

    @Override
    public List<Question> getReportedQuestions() {

            return questionRepository.findByReportedTrue();

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
        try {
            // 1. Authentification
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // 2. Récupération question
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question introuvable"));

            // 3. Création réponse
            answer.setUser(currentUser);
            answer.setQuestion(question);
            answer.setCreatedAt(new Date());
            Answer savedAnswer = answerRepository.save(answer);

            // 4. Envoi email
            sendNotificationEmail(question, currentUser);

            return savedAnswer;
        } catch (Exception e) {
            System.err.println("Erreur dans addAnswer: " + e.getMessage());
            throw e;
        }
    }

    private void sendNotificationEmail(Question question, User answerAuthor) {
        try {
            User questionOwner = question.getUser();

            if (!questionOwner.getId().equals(answerAuthor.getId())) {
                System.out.println("Préparation email pour: " + questionOwner.getEmail());

                String emailSubject = "💬 Nouvelle réponse à votre question !";
                String emailContent = String.format(
                        "Bonjour %s,\n\nUne nouvelle réponse a été ajoutée à votre question : \"%s\".\n\n" +
                                "Réponse de : %s %s\n\n" +
                                "Cordialement,\nL'équipe Support.",
                        questionOwner.getNom(),
                        question.getTitle(),
                        answerAuthor.getPrenom(),
                        answerAuthor.getNom()
                );

                if (emailService == null) {
                    System.err.println("EmailService non injecté !");
                    return;
                }

                emailService.sendEmail(questionOwner.getEmail(), emailSubject, emailContent);
                System.out.println("Email envoyé avec succès");
            } else {
                System.out.println("Pas d'email envoyé (réponse par l'auteur)");
            }
        } catch (Exception e) {
            System.err.println("Échec envoi email: " + e.getMessage());
        }
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

    public Question upvote(Long id) {
        return questionRepository.findById(id)
                .map(question -> {
                    question.setVote(question.getVote() + 1);
                    return questionRepository.save(question);
                })
                .orElse(null); // Retourne null si non trouvé
    }

    public Question downvote(Long id) {
        return questionRepository.findById(id)
                .map(question -> {
                    question.setVote(question.getVote() - 1);
                    return questionRepository.save(question);
                })
                .orElse(null); // Retourne null si non trouvé
    }
}





