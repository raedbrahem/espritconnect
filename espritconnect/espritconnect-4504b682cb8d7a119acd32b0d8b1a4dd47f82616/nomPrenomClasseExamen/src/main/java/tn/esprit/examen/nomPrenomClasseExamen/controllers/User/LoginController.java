package tn.esprit.examen.nomPrenomClasseExamen.controllers.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.LoginRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.CodeVerificationRequest;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.EmailServiceUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailServiceUser emailService;
    private final SecretKey secretKey;

    // Map temporaire pour stocker les codes
    private final Map<String, String> verificationCodes = new HashMap<>();

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           EmailServiceUser emailService,
                           @Value("${jwt.secret}") String secretKeyString) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.emailService = emailService;

        if (secretKeyString == null || secretKeyString.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured.");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 🔐 Étape 1 : login avec email + mot de passe → envoie du code par mail
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getMotDePasse()
                    )
            );

            // ✅ Génération du code à 6 chiffres
            String code = String.valueOf(new Random().nextInt(900000) + 100000);
            verificationCodes.put(loginRequest.getEmail(), code);

            // ✅ Envoi par mail
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String subject = "Votre code de vérification";
                String body = "Bonjour " + user.getNom() + ",\n\nVoici votre code : " + code + "\n\nL’équipe ESPRIT Connect.";
                emailService.sendSimpleEmail(loginRequest.getEmail(), subject, body);
            }

            return ResponseEntity.ok("Verification code sent to email.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    /**
     * ✅ Étape 2 : Vérification du code → génération du token JWT
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody CodeVerificationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String expectedCode = verificationCodes.get(email);

        if (expectedCode != null && expectedCode.equals(code)) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String role = (user.getRole() != null) ? user.getRole().name() : "USER";

                String token = generateToken(email, role);
                verificationCodes.remove(email);

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response); // ✅ renvoie du vrai JSON, pas texte
            }
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid verification code");
        return ResponseEntity.status(403).body(error);
    }

    /**
     * 🎟 Génération du token JWT
     */
    private String generateToken(String email, String role) {
        long expirationTime = 86400000; // 24h
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
}
