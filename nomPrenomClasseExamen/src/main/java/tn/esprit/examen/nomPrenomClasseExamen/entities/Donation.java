package tn.esprit.examen.nomPrenomClasseExamen.entities;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long donID;
    private float amount;
    private Date date;

    @Enumerated(EnumType.STRING)
    private DonationType type;

    @ManyToOne
    private Employe employee;

    public long getDonID() {
        return donID;
    }

    public void setDonID(long donID) {
        this.donID = donID;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DonationType getType() {
        return type;
    }

    public void setType(DonationType type) {
        this.type = type;
    }

    public Employe getEmployee() {
        return employee;
    }

    public void setEmployee(Employe employee) {
        this.employee = employee;
    }
}

