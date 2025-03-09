package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Service_Etude implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    User tutor;

    @Column(nullable = false)
    String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime publicationDate;

    @OneToMany(mappedBy = "serviceEtude", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Commentaire> commentaires = new ArrayList<>();

    @OneToMany(mappedBy = "serviceEtude", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<rating_etude> ratings = new ArrayList<>();

    // New ManyToMany relationship with User
    @ManyToMany(mappedBy = "serviceEtudesProvided")
    @JsonIgnore
    private List<User> clients;

    public List<User> getClients() {
        return clients;
    }

    public void setClients(List<User> clients) {
        this.clients = clients;
    }
}
