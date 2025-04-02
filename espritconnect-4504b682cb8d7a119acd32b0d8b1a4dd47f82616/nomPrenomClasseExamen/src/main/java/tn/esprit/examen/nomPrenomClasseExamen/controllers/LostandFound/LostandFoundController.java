package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound.IServiceLostandFound;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/item")

public class LostandFoundController {
    @Autowired
    private IServiceLostandFound serviceLostItem;
    private LostandFoundRepository itemRepository;
    // GET: Retrieve all lost items
    @GetMapping("/all")
    public List<Item> getLostItems() {
        return serviceLostItem.retrieveAllLostItems();
    }

    // GET: Retrieve a single lost item by ID
    @GetMapping("/{item-id}")
    public Item retrieveLostItem(@PathVariable("item-id") Long itemId) {
        return serviceLostItem.retrieveLostItem(itemId);
    }

    // POST: Add a new lost item
    @PostMapping("/add")
    public Item addLostItem(@RequestBody Item lostItem) {
        return serviceLostItem.addLostItem(lostItem);
    }

    // DELETE: Remove a lost item by ID
    @DeleteMapping("/remove/{item-id}")
    public void removeLostItem(@PathVariable("item-id") Long itemId) {
        serviceLostItem.removeLostItem(itemId);
    }

    // PUT: Modify an existing lost item
    @PutMapping("/modify")
    public Item modifyLostItem(@RequestBody Item lostItem) {
        return serviceLostItem.modifyLostItem(lostItem);
    }

    @GetMapping("/category/{cat}")
    public List<Item> getItemsByCategory(@PathVariable("cat") CategoryItem category) {
        return itemRepository.findByCategory(category);
    }
    // GET: Find items by location

    // GET: Find unfound items
    //@GetMapping("/unfound-items")
    //public List<Item> findUnfoundItems() {
        //     return serviceLostItem.findUnfoundItems();
    //}
}
