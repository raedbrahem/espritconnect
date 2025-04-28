package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Status;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.TypeM;

import java.io.IOException;
import java.util.List;

public interface IFoyerServices {

    List<Foyer> getAllFoyers();

    Foyer getFoyerById(Long id);
    Foyer updateFoyer(Long id, Foyer updatedFoyer, MultipartFile image);
    void deleteFoyer(Long id);
    List<Foyer> getFoyersByCurrentUser();



     Foyer uploadFoyer(Foyer foyer, MultipartFile image) ;


        List<Foyer> searchFoyersByLocation(String location);
    List<Foyer> searchFoyersByPrice(Double prixMin, Double prixMax);
}
