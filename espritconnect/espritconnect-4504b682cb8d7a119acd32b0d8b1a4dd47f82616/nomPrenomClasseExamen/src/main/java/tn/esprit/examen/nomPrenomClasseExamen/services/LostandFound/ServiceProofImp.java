package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.ProofRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceProofImp implements IServiceProof{
    private final ProofRepository proofRepository;

    @Override
    public List<Proof> retrieveAllProofItems() {
        return proofRepository.findAll();
    }

    @Override
    public Proof retrieveProofItem(Long id) {
        return proofRepository.findById(id).orElse(null);
    }

    @Override
    public Proof addProofItem(Proof proof) {
        return proofRepository.save(proof);
    }

    @Override
    public void removeProofItem(Long id) {
        proofRepository.deleteById(id);
    }

    @Override
    public Proof modifyProofItem(Proof proof) {
        return proofRepository.save(proof);
    }
}
