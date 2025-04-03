package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Date;
import java.util.Set;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;


    String content;
    String screenshot;
    Date createdAt;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    Tag tag;
    @ManyToOne
    @JsonIgnore
    User user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    @JsonIgnore
    public Set<Answer> answers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    @JsonIgnore
    public Set<Notificationn> notifications;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    @JsonIgnore
    public Set<Vote> votes;

    public Question(int id, String title, String content, String screenshot, Date createdAt, Tag tag, User user, Set<Answer> answers, Set<Notificationn> notifications) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.screenshot = screenshot;
        this.createdAt = createdAt;
        this.tag = tag;
        this.user = user;
        this.answers = answers;
        this.notifications = notifications;
    }

    public Question() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Set<Notificationn> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notificationn> notifications) {
        this.notifications = notifications;
    }
}