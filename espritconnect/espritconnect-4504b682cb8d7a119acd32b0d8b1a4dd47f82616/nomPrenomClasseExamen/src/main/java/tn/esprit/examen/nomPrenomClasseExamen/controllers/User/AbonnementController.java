package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.UserDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.UserMapper;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.AbonnementService;

import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AbonnementController {
    private final AbonnementService abonnementService;
    private final UserMapper userMapper;

    @PostMapping("/follow/{id}")
    public ResponseEntity<?> follow(@AuthenticationPrincipal User user, @PathVariable Long id) {
        abonnementService.follow(user, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{id}")
    public ResponseEntity<?> unfollow(@AuthenticationPrincipal User user, @PathVariable Long id) {
        abonnementService.unfollow(user, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-following/{id}")
    public ResponseEntity<Boolean> isFollowing(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(abonnementService.isFollowing(user, id));
    }

    @GetMapping("/followers")
    public List<UserDTO> getFollowers(@AuthenticationPrincipal User user) {
        return abonnementService.getFollowers(user, userMapper);
    }

    @GetMapping("/followees")
    public List<UserDTO> getFollowees(@AuthenticationPrincipal User user) {
        return abonnementService.getFollowees(user, userMapper);
    }
    @GetMapping("/users/{userId}/followers-count")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Long userId) {
        int count = abonnementService.getFollowersCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/users/{userId}/followees-count")
    public ResponseEntity<Integer> getFolloweesCount(@PathVariable Long userId) {
        int count = abonnementService.getFolloweesCount(userId);
        return ResponseEntity.ok(count);
    }
}
