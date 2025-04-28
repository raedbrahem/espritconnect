package tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;

import java.util.List;
import java.util.UUID;

public interface LostandFoundRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(CategoryItem category);

    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.category = :category WHERE i.id_item = :itemId")
    int updateCategory(@Param("itemId") Long itemId, @Param("category") CategoryItem category);
}
