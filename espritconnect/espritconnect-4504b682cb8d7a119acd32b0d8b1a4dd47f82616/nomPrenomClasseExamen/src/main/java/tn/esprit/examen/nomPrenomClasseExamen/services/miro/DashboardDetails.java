package tn.esprit.examen.nomPrenomClasseExamen.services.miro;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardDetails {
    private String viewLink;

    @JsonProperty("viewLink")
    public String getViewLink() {
        return viewLink;
    }

    public void setViewLink(String viewLink) {
        this.viewLink = viewLink;
    }
}