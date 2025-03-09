package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceLostandFound;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/lost-item")

public class LostandFoundController {
    @Autowired
    private IServiceLostandFound serviceLostItem;

    // GET: Retrieve all lost items
    @GetMapping("/retrieve-all-lost-items")
    public List<Item> getLostItems() {
        return serviceLostItem.retrieveAllLostItems();
    }

    // GET: Retrieve a single lost item by ID
    @GetMapping("/retrieve-lost-item/{item-id}")
    public Item retrieveLostItem(@PathVariable("item-id") Long itemId) {
        return serviceLostItem.retrieveLostItem(itemId);
    }

    // POST: Add a new lost item
    @PostMapping("/add-lost-item")
    public Item addLostItem(@RequestBody Item lostItem) {
        return serviceLostItem.addLostItem(lostItem);
    }

    // DELETE: Remove a lost item by ID
    @DeleteMapping("/remove-lost-item/{item-id}")
    public void removeLostItem(@PathVariable("item-id") Long itemId) {
        serviceLostItem.removeLostItem(itemId);
    }

    // PUT: Modify an existing lost item
    @PutMapping("/modify-lost-item")
    public Item modifyLostItem(@RequestBody Item lostItem) {
        return serviceLostItem.modifyLostItem(lostItem);
    }

    // GET: Find items by location
       @GetMapping("/location/{location}")
       public List<Item> findLostItemsByLocation(@PathVariable String location) {
        return serviceLostItem.findLostItemsByLocation(location);
    }

    // GET: Find unfound items
    //@GetMapping("/unfound-items")
    //public List<Item> findUnfoundItems() {
        //     return serviceLostItem.findUnfoundItems();
    //}
}
