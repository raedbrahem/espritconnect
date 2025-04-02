package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Date;
import java.util.Set;

@Entity
public class Answer {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String content;
    Date createdAt;

    @ManyToOne
    @JsonIgnore
    User user;
    @ManyToOne
    @JsonIgnore
    Question question;
    @OneToMany( cascade = CascadeType.ALL ,mappedBy = "answer")
    private Set<Vote> votes;

    public Answer(int id, String content, Date createdAt, User user, Question question, Set<Vote> votes) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
        this.question = question;
        this.votes = votes;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Answer() {
    }
}
