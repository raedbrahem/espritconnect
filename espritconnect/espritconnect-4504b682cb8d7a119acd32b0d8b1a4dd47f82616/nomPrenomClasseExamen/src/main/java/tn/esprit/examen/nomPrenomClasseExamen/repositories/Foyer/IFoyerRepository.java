package tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;

import java.util.List;

@Repository
public interface IFoyerRepository extends JpaRepository<Foyer, Long> {

    List<Foyer> findByUser(User user);
    List<Foyer> findByLocalisationStartingWith(String location);
    List<Foyer> findByPrixBetween(Double prixMin, Double prixMax);

    // Rechercher les foyers dont le prix est supérieur ou égal à prixMin
    List<Foyer> findByPrixGreaterThanEqual(Double prixMin);

    // Rechercher les foyers dont le prix est inférieur ou égal à prixMax
    List<Foyer> findByPrixLessThanEqual(Double prixMax);

    List<Foyer> findByUserId(Long userId);

}
