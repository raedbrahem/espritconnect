package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Client;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Projet;

import java.util.List;

public interface IServices {
    List<Projet> retrieveall();
    Projet retrieve(Long foyerId);
    Projet add(Projet foyer);
    void remove(Long foyerId);
    Projet modify(Projet foyer);
}
