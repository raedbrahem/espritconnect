package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create a new order
    public Order createOrder(Order order) {
        return orderRepository.save(order);  // Save the order with finalPrice, orderDate, and product
    }

    // Update an existing order (we will not update product for now)
    public Order updateOrder(Long id, Order updatedOrder) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setFinalPrice(updatedOrder.getFinalPrice());
            order.setProduct(updatedOrder.getProduct());
            return orderRepository.save(order);
        }
        return null;
    }

    // Delete an order by ID
    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get an order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
