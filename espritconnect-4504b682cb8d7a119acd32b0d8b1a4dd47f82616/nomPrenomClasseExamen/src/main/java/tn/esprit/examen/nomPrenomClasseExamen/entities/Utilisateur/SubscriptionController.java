package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final UserService userService;

    @Autowired
    public SubscriptionController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint pour suivre un utilisateur
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody Map<String, Long> request) {
        Long followerId = request.get("followerId");
        Long followeeId = request.get("followeeId");
        userService.followUser(followerId, followeeId);
        return ResponseEntity.ok("Utilisateur suivi avec succès.");
    }

    // Endpoint pour se désabonner d'un utilisateur
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody Map<String, Long> request) {
        Long followerId = request.get("followerId");
        Long followeeId = request.get("followeeId");
        userService.unfollowUser(followerId, followeeId);
        return ResponseEntity.ok("Désabonnement effectué avec succès.");
    }

    // Endpoint pour obtenir les abonnés (followers) d'un utilisateur
    @GetMapping("/users/{id}/followers")
    public ResponseEntity<Set<User>> getFollowers(@PathVariable Long id) {
        Set<User> followers = userService.getFollowers(id);
        return ResponseEntity.ok(followers);
    }

    // Endpoint pour obtenir les abonnements (followees) d'un utilisateur
    @GetMapping("/users/{id}/followees")
    public ResponseEntity<Set<User>> getFollowees(@PathVariable Long id) {
        Set<User> followees = userService.getFollowees(id);
        return ResponseEntity.ok(followees);
    }
}
