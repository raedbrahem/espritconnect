package tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // Marks this class as a JPA entity
public class Call {
    @Id // Marks this field as the primary key
    private String callId; // Primary key of type String

    private String offer;
    private String answer;

    @ElementCollection // Maps the list of strings as a collection of basic types
    @CollectionTable(name = "call_candidates", joinColumns = @JoinColumn(name = "call_id")) // Specifies the table for candidates
    @Column(name = "candidate") // Specifies the column name for candidates
    private List<String> candidates = new ArrayList<>();

    // No-argument constructor required by JPA
    public Call() {
    }

    // Constructor with parameters
    public Call(String callId, String offer) {
        this.callId = callId;
        this.offer = offer;
    }

    // Getters and setters
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }
}