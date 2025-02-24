package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Projet;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ProjetRepository;

import java.util.List;

@Service
public class ProjetServiceImpl implements  IServices{
    @Autowired
    private ProjetRepository projetRepository;
    @Override
    public List<Projet> retrieveall() {
        return projetRepository.findAll();
    }

    @Override
    public Projet retrieve(Long projetid) {
        return projetRepository.findById(projetid).get();
    }

    @Override
    public Projet add(Projet projet) {
        return projetRepository.save(projet);
    }

    @Override
    public void remove(Long projetid) {
        projetRepository.deleteById(projetid);
    }

    @Override
    public Projet modify(Projet foyer) {
        return projetRepository.save(foyer);
    }
}
