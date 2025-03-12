package tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.webrtc;

import java.util.ArrayList;
import java.util.List;

public class Call {

    private String offer;
    private String answer;
    private List<String> candidates = new ArrayList<>();

    // No-argument constructor required by JPA
    public Call() {
    }

    // Constructor with parameters
    public Call(String callId, String offer) {
        this.offer = offer;
    }

    // Getters and setters

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