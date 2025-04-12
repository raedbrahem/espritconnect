package tn.esprit.examen.nomPrenomClasseExamen.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Abonnement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;
import java.util.Optional;
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    boolean existsByFollowerAndFollowee(User follower, User followee);
    Optional<Abonnement> findByFollowerAndFollowee(User follower, User followee);

    List<Abonnement> findByFollower(User follower);
    List<Abonnement> findByFollowee(User followee);
    @Query("SELECT COUNT(a) FROM Abonnement a WHERE a.followee.id = :userId")
    int countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Abonnement a WHERE a.follower.id = :userId")
    int countFolloweesByUserId(@Param("userId") Long userId);

}
