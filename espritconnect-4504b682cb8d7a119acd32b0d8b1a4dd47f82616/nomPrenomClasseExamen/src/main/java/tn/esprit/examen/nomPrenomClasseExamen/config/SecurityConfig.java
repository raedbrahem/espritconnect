package tn.esprit.examen.nomPrenomClasseExamen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Autoriser ces endpoints sans authentification
                        .requestMatchers("/api/register", "/api/login", "/api/forgot-password", "/api/reset-password", "/uploads/**").permitAll()

                        // Authentification requise
                        .requestMatchers("/api/users/profile", "/api/users/profile/update", "/api/users/email/**", "/api/users/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 🔥 Autoriser follow/unfollow/is-following
                        .requestMatchers(
                                "/api/follow/**",
                                "/api/unfollow/**",
                                "/api/is-following/**",
                                "/api/users/**/followers",
                                "/api/users/**/followees",
                                "/api/users/**/followers-count",
                                "/api/users/**/followees-count"
                        ).authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(UserService userService) {
        return new JwtTokenFilter(userService);
    }
}
