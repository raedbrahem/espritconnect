package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ProjetDetail;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ProjetDetailRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProjetDetailServiceImpl implements IServiceProjetDetail {

    @Autowired
    private ProjetDetailRepository projetDetailRepository;

    @Override
    public List<ProjetDetail> getProjetDetails() {
        return projetDetailRepository.findAll();
    }

    @Override
    public ProjetDetail retrieve(Long id) {
        return projetDetailRepository.findById(id).get();
    }

    @Override
    public ProjetDetail add(ProjetDetail projetDetail) {
        return projetDetailRepository.save(projetDetail);
    }

    @Override
    public void remove(Long id) {
        projetDetailRepository.deleteById(id);
    }

    @Override
    public ProjetDetail modify(ProjetDetail projetDetail) {
            return projetDetailRepository.save(projetDetail);
    }
}
