package tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Status; // Assure-toi que l'enum Status existe bien
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String localisation;
    private Double latitude;

    private Double longitude;
    private Double superficie;
    private Double prix;
    private Integer nbrDeChambre;

    @Enumerated(EnumType.STRING)
    private TypeM typeM;

    private Boolean meuble;
    private String googleMapsLink;
    private LocalDate datePublication = LocalDate.now();

    // Attribut pour stocker le chemin de l'image
    private String image;

    @Enumerated(EnumType.STRING)
    private Status status = Status.EN_ATTENTE; // Valeur par défaut

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "foyer", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NotificationF> notifications = new ArrayList<>();
    @OneToMany(mappedBy = "foyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReservationF> reservations = new ArrayList<>();


    // Méthode toString pour éviter la récursion infinie
    @Override
    public String toString() {
        return "Foyer{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", localisation='" + localisation + '\'' +
                ", superficie=" + superficie +
                ", prix=" + prix +
                ", nbrDeChambre=" + nbrDeChambre +
                ", typeM=" + typeM +
                ", meuble=" + meuble +
                ", googleMapsLink='" + googleMapsLink + '\'' +
                ", datePublication=" + datePublication +
                ", image='" + image + '\'' +
                ", status=" + status +
                '}'; // Ajout du champ dans toString
    }


}