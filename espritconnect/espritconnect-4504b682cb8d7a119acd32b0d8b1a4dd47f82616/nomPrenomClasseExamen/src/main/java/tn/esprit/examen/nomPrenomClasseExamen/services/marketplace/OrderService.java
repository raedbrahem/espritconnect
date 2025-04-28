package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Order;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.OrderRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
    @Transactional
    public boolean deleteOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            // Get the product associated with this order
            Product product = order.getProduct();

            // First, remove the order
            orderRepository.delete(order);

            // Log the deletion
            System.out.println("Order deleted successfully: " + id);

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
