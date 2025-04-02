package tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
@Repository

public interface AnswerRepository extends JpaRepository<Answer,Long> {
}
