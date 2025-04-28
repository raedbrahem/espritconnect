package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.Date;




@Entity
public class Answer {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String content;
    Date createdAt;
    private Integer scoreIA;         // Le score calculé par IA
    @Column(columnDefinition = "TEXT")
    private String commentaireIA;    // Le feedback de l'IA

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"answers", "questions", "followees", "followers"}) // Évite la récursion
    private User user; // Ce champ sera sérialisé

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;

    public Answer(long id, String content, Date createdAt, Integer scoreIA, String commentaireIA, User user, Question question) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.scoreIA = scoreIA;
        this.commentaireIA = commentaireIA;
        this.user = user;
        this.question = question;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public Answer() {
    }

    public Integer getScoreIA() {
        return scoreIA;
    }

    public void setScoreIA(Integer scoreIA) {
        this.scoreIA = scoreIA;
    }

    public String getCommentaireIA() {
        return commentaireIA;
    }

    public void setCommentaireIA(String commentaireIA) {
        this.commentaireIA = commentaireIA;
    }
}
