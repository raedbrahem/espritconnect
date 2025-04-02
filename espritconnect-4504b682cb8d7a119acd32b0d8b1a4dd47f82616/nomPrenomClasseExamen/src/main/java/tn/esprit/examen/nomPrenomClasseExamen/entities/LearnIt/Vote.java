package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;

import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Date;

@Entity

public class Vote {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

     int value;

    @Temporal(TemporalType.TIMESTAMP) // Spécifie que le champ est de type TIMESTAMP
    @Column(name = "created_at", nullable = false, updatable = false) // Ne peut pas être mis à jour
     Date createdAt;
    @ManyToOne(cascade = CascadeType.ALL)
    User user;

    @ManyToOne(cascade = CascadeType.ALL)
    Question question;
  @ManyToOne(cascade = CascadeType.ALL)
  Answer answer;


    public Vote(Long id, int value, Date createdAt, User user, Question question) {
        this.id = id;
        this.value = value;
        this.createdAt = createdAt;
        this.user = user;
        this.question = question;
    }

    public Vote() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
