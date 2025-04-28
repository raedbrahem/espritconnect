package tn.esprit.examen.nomPrenomClasseExamen.services.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.config.JwtUtil;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.AbonnementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    private AbonnementRepository abonnementRepository;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);

        if (optionalUser.isEmpty()) {
            System.out.println("⚠️ Utilisateur introuvable avec email: " + username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        User user = optionalUser.get();
        System.out.println("✅ Utilisateur trouvé : " + user.getEmail());

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

        // Si le rôle n'est pas défini, on met USER par défaut
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

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


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    // Dans UserService.java
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User processOAuth2User(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setNom(oauthUser.getAttribute("name"));
            user.setPhotoProfil(oauthUser.getAttribute("picture"));
            user.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString())); // faux mdp
            user.setRole(Role.USER); // ✅ TRÈS IMPORTANT
            return userRepository.save(user);
        });
    }
    public User save(User user) {
        return userRepository.save(user);
    }
    public String jwtForEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return jwtUtil.generateToken(user);
    }




}
