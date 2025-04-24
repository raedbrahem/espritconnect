package tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_item;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private User proprietaire;


    String item_name;
    String item_image;
    String description;
    @Enumerated(EnumType.STRING)
    CategoryItem category;
    String lieuPerdu;
    LocalDate datePerdu;
    boolean retrouve;
    LocalDateTime datePublication_item;
    private Boolean matchValidated = false;
    private Boolean matchAccepted = null;


    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private Proof proof;



    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacks;

}

