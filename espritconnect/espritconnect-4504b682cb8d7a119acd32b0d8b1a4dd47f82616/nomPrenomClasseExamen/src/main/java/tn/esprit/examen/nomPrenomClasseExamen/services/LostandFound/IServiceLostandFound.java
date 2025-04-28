package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

public interface IServiceLostandFound {
    List<Item> retrieveAllLostItems();
    Item retrieveLostItem(Long id);
    Item addLostItem(Item lostItem);
    void deleteItem(Long id);
    ResponseEntity<Object> uploadItemOrProof(
            String name,
            String description,
            LocalDate datePerdu,
            String lieuPerdu,
            boolean retrouve,
            MultipartFile imageFile
    ) throws IOException;

    Item updateItem(Long id, String name, String description, String datePerdu, String lieuPerdu, boolean retrouve, MultipartFile image) throws IOException;
}
