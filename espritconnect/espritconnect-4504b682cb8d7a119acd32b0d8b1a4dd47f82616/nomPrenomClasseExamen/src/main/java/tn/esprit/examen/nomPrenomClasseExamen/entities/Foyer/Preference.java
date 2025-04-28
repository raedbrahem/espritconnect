package tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User; // ✅ Import correct de User

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double prixMin;
    private Double prixMax;

    @Enumerated(EnumType.STRING)
    private TypeM typeM; // Type de logement (Appartement, Studio, etc.)

    private String localisation; // L'adresse de la préférence


    private Boolean meuble; // True si le logement est meublé, false sinon


    // Nouveaux attributs pour les coordonnées géographiques
    private Double latitude;
    private Double longitude;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

}
