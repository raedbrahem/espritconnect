package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Proof;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceProof;

import java.util.List;

@RestController
@RequestMapping("/proof")
@RequiredArgsConstructor

public class ProofController {
    private final IServiceProof proofService;

    @GetMapping("/all")
    public List<Proof> getAllProofs() {
        return proofService.retrieveAllProofItems();
    }

    @GetMapping("/{id}")
    public Proof getProofById(@PathVariable Long id) {
        return proofService.retrieveProofItem(id);
    }

    @PostMapping("/add")
    public Proof addProof(@RequestBody Proof proof) {
        return proofService.addProofItem(proof);
    }

    @PutMapping("/update")
    public Proof updateProof(@RequestBody Proof proof) {
        return proofService.modifyProofItem(proof);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProof(@PathVariable Long id) {
        proofService.removeProofItem(id);
    }
}
