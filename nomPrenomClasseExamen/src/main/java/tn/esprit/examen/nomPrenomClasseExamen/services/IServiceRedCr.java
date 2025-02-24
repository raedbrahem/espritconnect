package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.ProjetDetail;
import tn.esprit.examen.nomPrenomClasseExamen.entities.RedCrescent;

import java.util.List;

public interface IServiceRedCr {
    List<RedCrescent> getredCrescentDetails();
    RedCrescent retrieve(Long id);
    RedCrescent add(RedCrescent redCrescent);
    void remove(Long Id);
    RedCrescent modify(RedCrescent redCrescent);
}
