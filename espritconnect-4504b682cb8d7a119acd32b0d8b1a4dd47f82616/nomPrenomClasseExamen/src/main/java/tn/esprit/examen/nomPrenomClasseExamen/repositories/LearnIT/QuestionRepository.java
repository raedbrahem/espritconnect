package tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;

@Repository

public interface QuestionRepository extends JpaRepository<Question,Long> {
}
