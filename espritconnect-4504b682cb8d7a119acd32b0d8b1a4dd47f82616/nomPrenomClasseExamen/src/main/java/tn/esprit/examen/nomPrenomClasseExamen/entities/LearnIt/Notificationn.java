package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;

import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Date;

@Entity

public class Notificationn {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    
    @ManyToOne(cascade = CascadeType.ALL)
    User user;
    @ManyToOne(cascade = CascadeType.ALL)
    Answer answer;
    @ManyToOne(cascade = CascadeType.ALL)
    Question question;

    public Notificationn(Long id, String content, NotificationType type, Date createdAt, User user, Answer answer, Question question) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.user = user;
        this.answer = answer;
        this.question = question;
    }

    public Notificationn() {
    }



    public Notificationn(String content, NotificationType notificationType, User user, Question question) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
