package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class rating_etude implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    int stars; // Rating value from 1 to 5

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime dateRating;

    @Column(nullable = false)
    Long userId; // ID of the student who rated

    // Many-to-One relationship with Service_Etude
    @ManyToOne
    @JoinColumn(name = "service_etude_id", nullable = false)
    Service_Etude serviceEtude;
}
