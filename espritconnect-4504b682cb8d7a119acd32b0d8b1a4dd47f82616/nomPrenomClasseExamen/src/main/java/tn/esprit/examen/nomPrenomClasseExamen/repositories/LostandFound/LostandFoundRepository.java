package tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;

import java.util.List;
import java.util.UUID;

public interface LostandFoundRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(CategoryItem category);

}
