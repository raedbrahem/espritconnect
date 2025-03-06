package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder // Optional: For builder pattern support
@Entity
public class Service_Etude implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use UUID for ID generation
    long id;

    @Column(nullable = false)
    long tutorId; // Simple UUID attribute instead of a foreign key

    @Column(nullable = false)
    String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @CreationTimestamp // Automatically sets the publication date
    @Column(nullable = false, updatable = false)
    LocalDateTime publicationDate;


    @OneToMany(mappedBy = "serviceEtude")
    @ToStringExclude
    @JsonIgnore
    List<Commentaire> commentaires;

    @OneToMany(mappedBy = "serviceEtude")
    @JsonIgnore // Prevent serialization issues (optional)
    @ToStringExclude // Prevent circular reference in toString (optional)
    List<rating_etude> ratings;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTutorId() {
        return tutorId;
    }

    public void setTutorId(long tutorId) {
        this.tutorId = tutorId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

}