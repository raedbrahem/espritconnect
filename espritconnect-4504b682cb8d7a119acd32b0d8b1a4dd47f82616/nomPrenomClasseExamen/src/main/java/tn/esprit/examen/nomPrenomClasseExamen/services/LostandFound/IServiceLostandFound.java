package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;

import java.util.UUID;
import java.util.List;

public interface IServiceLostandFound {
    List<Item> retrieveAllLostItems();
    Item retrieveLostItem(Long id);
    Item addLostItem(Item lostItem);
    void removeLostItem(Long id);
    Item modifyLostItem(Item lostItem);
    List<Item> findLostItemsByLocation(String location);

}
