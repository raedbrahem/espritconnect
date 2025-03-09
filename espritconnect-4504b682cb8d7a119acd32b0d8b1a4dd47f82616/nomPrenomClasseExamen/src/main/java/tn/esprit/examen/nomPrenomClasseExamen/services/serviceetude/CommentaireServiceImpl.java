package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.serviceetude.Commentaire;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.CommentaireRepository;

import java.util.List;

@Service
public class CommentaireServiceImpl implements ICommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Override
    public Commentaire addCommentaire(Commentaire commentaire) {
        return commentaireRepository.save(commentaire);
    }

    @Override
    public void deleteCommentaire(Commentaire commentaire) {
        commentaireRepository.delete(commentaire);
    }

    @Override
    public Commentaire updateCommentaire(Commentaire commentaire) {
            return commentaireRepository.save(commentaire);
    }

    @Override
    public List<Commentaire> getAllCommentaires() {
        return commentaireRepository.findAll();
    }

    @Override
    public Commentaire retrieveCommentaire(Long id) {
        return commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire not found with ID: " + id));
    }
}