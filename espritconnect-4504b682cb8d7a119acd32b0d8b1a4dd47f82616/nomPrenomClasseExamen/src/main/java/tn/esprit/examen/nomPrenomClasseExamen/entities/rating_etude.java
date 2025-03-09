package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utlisateur.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "service_etude_id"})) // Enforce uniqueness
public class rating_etude implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    int stars; // Rating value from 1 to 5

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime dateRating;

    // Many-to-One relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Many-to-One relationship with Service_Etude
    @ManyToOne
    @JoinColumn(name = "service_etude_id", nullable = false)
    Service_Etude serviceEtude;
}
