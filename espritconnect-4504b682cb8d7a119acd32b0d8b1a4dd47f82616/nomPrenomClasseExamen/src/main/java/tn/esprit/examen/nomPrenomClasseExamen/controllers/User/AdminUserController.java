package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // Nouveau endpoint pour la recherche d'utilisateurs
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("keyword") String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }
}