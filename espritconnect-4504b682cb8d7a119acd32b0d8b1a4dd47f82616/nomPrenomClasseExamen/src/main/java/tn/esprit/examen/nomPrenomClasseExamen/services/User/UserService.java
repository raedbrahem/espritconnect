package tn.esprit.examen.nomPrenomClasseExamen.services.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Answer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Notificationn;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Question;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt.Vote;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        user.setDateInscription(new Date());
        user.setStatutVerification("en attente");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setNom(updatedUser.getNom());
        user.setPrenom(updatedUser.getPrenom());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getMotDePasse() != null && !updatedUser.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(updatedUser.getMotDePasse()));
        }
        user.setTelephone(updatedUser.getTelephone());
        user.setNiveauEtude(updatedUser.getNiveauEtude());
        user.setAdresse(updatedUser.getAdresse());
        user.setPhotoProfil(updatedUser.getPhotoProfil());
        user.setCarteEtudiant(updatedUser.getCarteEtudiant());
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Nouvelle méthode de recherche
    public List<User> searchUsers(String keyword) {
        return userRepository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // --- Fonctionnalités d'abonnement ---

    // Suivre un utilisateur
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        User follower = findById(followerId);
        User followee = findById(followeeId);
        // La collection followees est initialisée dans une session transactionnelle
        follower.getFollowees().add(followee);
        userRepository.save(follower);
    }

    // Se désabonner d'un utilisateur
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        User follower = findById(followerId);
        User followee = findById(followeeId);
        follower.getFollowees().remove(followee);
        userRepository.save(follower);
    }

    // Récupérer les abonnés (followers) d'un utilisateur
    @Transactional(readOnly = true)
    public Set<User> getFollowers(Long userId) {
        User user = findById(userId);
        return user.getFollowers();
    }

    // Récupérer les abonnements (followees) d'un utilisateur
    @Transactional(readOnly = true)
    public Set<User> getFollowees(Long userId) {
        User user = findById(userId);
        return user.getFollowees();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    // Dans UserService.java
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    ////foued///////////
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    public Set<Question> questions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    public Set<Answer> answers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    public Set<Notificationn> notifications;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    public Set<Vote> votes;
}
