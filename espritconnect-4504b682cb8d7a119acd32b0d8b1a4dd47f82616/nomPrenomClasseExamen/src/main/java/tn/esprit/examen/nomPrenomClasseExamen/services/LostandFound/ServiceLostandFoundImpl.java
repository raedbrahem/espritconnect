package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceLostandFoundImpl implements IServiceLostandFound{

    @Autowired
    private LostandFoundRepository lostItemRepository;

    @Override
    public List<Item> retrieveAllLostItems() {
        return lostItemRepository.findAll();
    }

    @Override
    public Item retrieveLostItem(Long id) {
        return lostItemRepository.findById(id).orElse(null);
    }

    @Override
    public Item addLostItem(Item lostItem) {
        lostItem.setDatePublication_item(LocalDateTime.now());
        return lostItemRepository.save(lostItem);
    }

    @Override
    public void removeLostItem(Long id) {
        lostItemRepository.deleteById(id);
    }

    @Override
    public Item modifyLostItem(Item lostItem) {
        return lostItemRepository.save(lostItem);
    }

    @Override
    public List<Item> findLostItemsByLocation(String location) {
        return lostItemRepository.findByLieuPerdu(location);
    }

}

