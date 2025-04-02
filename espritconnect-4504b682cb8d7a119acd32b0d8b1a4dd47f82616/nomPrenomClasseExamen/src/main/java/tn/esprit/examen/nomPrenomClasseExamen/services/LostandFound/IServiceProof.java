package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;

import java.util.List;

public interface IServiceProof {
    List<Proof> retrieveAllProofItems();

    Proof retrieveProofItem(Long id);

    Proof addProofItem(Proof proof);

    void removeProofItem(Long id);

    Proof modifyProofItem(Proof proof);
    }


