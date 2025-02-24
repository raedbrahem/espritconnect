package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.RedCrescent;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.redcrescentrepository;

import java.util.List;
@Service
public class redCrescentServiceImpl implements IServiceRedCr {

    @Autowired
    private redcrescentrepository redCrescentRepository;

    @Override
    public List<RedCrescent> getredCrescentDetails() {
        return redCrescentRepository.findAll();
    }

    @Override
    public RedCrescent retrieve(Long id) {
        return redCrescentRepository.findById(id).orElse(null);
    }

    @Override
    public RedCrescent add(RedCrescent redCrescent) {
        return redCrescentRepository.save(redCrescent);
    }

    @Override
    public void remove(Long id) {
        redCrescentRepository.deleteById(id);
    }

    @Override
    public RedCrescent modify(RedCrescent redCrescent) {
        return redCrescentRepository.save(redCrescent);
    }
}
