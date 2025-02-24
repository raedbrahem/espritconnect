package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class RedCrescent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long redCID;
    private String area;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Donation> donations;

    public long getRedCID() {
        return redCID;
    }

    public void setRedCID(long redCID) {
        this.redCID = redCID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}
