package tn.esprit.examen.nomPrenomClasseExamen.services.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Abonnement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.UserDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.UserMapper;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.AbonnementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AbonnementService {
    private final AbonnementRepository abonnementRepo;
    private final UserRepository userRepo;

    public void follow(User follower, Long followeeId) {
        User followee = userRepo.findById(followeeId).orElseThrow();
        if (!abonnementRepo.existsByFollowerAndFollowee(follower, followee)) {
            Abonnement ab = new Abonnement();
            ab.setFollower(follower);
            ab.setFollowee(followee);
            ab.setDateAbonnement(LocalDateTime.now());
            abonnementRepo.save(ab);
        }
    }

    public void unfollow(User follower, Long followeeId) {
        User followee = userRepo.findById(followeeId).orElseThrow();
        abonnementRepo.findByFollowerAndFollowee(follower, followee)
                .ifPresent(abonnementRepo::delete);
    }

    public boolean isFollowing(User follower, Long followeeId) {
        User followee = userRepo.findById(followeeId).orElseThrow();
        return abonnementRepo.existsByFollowerAndFollowee(follower, followee);
    }

    public List<UserDTO> getFollowees(User follower, UserMapper mapper) {
        return abonnementRepo.findByFollower(follower).stream()
                .map(Abonnement::getFollowee)
                .map(mapper::toDTO)
                .toList();
    }

    public List<UserDTO> getFollowers(User followee, UserMapper mapper) {
        return abonnementRepo.findByFollowee(followee).stream()
                .map(Abonnement::getFollower)
                .map(mapper::toDTO)
                .toList();
    }
    public int getFollowersCount(Long userId) {
        return abonnementRepo.countFollowersByUserId(userId);
    }

    public int getFolloweesCount(Long userId) {
        return abonnementRepo.countFolloweesByUserId(userId);
    }

}
