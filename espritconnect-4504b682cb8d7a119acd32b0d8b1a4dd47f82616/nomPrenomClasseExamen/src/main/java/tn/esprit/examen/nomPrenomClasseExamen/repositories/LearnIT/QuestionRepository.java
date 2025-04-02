package tn.esprit.examen.nomPrenomClasseExamen.repositories.LearnIT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Tag;

import java.util.List;

@Repository

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByTag(Tag tag);
}
