package tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;

import java.util.List;
import org.springframework.data.jpa.repository.*;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE f.item.id_item = :itemId")
    List<Feedback> findByItemId(@Param("itemId") Long itemId);
    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId")
    List<Feedback> findByUserId(@Param("userId") Long userId);}