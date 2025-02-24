package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Projet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ProjetDetail;

import java.util.List;

public interface IServiceProjetDetail {
    List<ProjetDetail> getProjetDetails();
    ProjetDetail retrieve(Long id);
    ProjetDetail add(ProjetDetail projetDetail);
    void remove(Long Id);
    ProjetDetail modify(ProjetDetail projetDetail);

}
