package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;

import java.util.List;
@Service
public class PaiementServiceImpl implements IServicePaiement{
    private final PaiementRepository paiementRepository;

    @Autowired
    public PaiementServiceImpl(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public List<Paiement> retrieveAllPaiements() {
        return paiementRepository.findAll();
    }

    @Override
    public Paiement retrievePaiement(Long id_paiement) {
        return paiementRepository.findById(id_paiement).orElse(null);
    }

    @Override
    public Paiement addPaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    public void removePaiement(Long id_paiement) {
        paiementRepository.deleteById(id_paiement);
    }

    @Override
    public Paiement modifyPaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }


}
