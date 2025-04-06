package tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace;

import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
