package tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.TypeTrajet;

import java.util.List;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    @Query("SELECT t.typeTrajet, COUNT(t) FROM Trajet t GROUP BY t.typeTrajet")
    List<Object[]> countTrajetsByType();

    @Query("SELECT t FROM Trajet t WHERE t.conducteur.id = :userId")
    List<Trajet> findTrajetsByUserId(@Param("userId") Long userId);

    @Query("SELECT t.typeTrajet, COUNT(t) FROM Trajet t WHERE t.conducteur.id = :userId GROUP BY t.typeTrajet")
    List<Object[]> countTrajetsByUserAndType(@Param("userId") Long userId);

    @Query("SELECT t FROM Trajet t JOIN FETCH t.conducteur WHERE t.id_trajet = :idTrajet")
    Optional<Trajet> findByIdWithConducteur(@Param("idTrajet") Long idTrajet);


}
