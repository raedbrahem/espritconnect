package tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Vote;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Trouver un vote par utilisateur et question
    Optional<Vote> findByUserAndQuestion(User user, Question question);

    // Compter les votes positifs pour une question
    int countByQuestionAndValue(Question question, int value);

    // Compter les votes négatifs pour une question
    int countByQuestionAndValueIs(Question question, int value);
}
