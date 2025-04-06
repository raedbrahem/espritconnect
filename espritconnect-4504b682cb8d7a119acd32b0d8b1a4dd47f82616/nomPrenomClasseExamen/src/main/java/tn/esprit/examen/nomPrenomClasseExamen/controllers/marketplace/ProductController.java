package tn.esprit.examen.nomPrenomClasseExamen.controllers.marketplace;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.dto.ProductDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.ProductService;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    // Inject both services into the constructor
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // Get all products
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }


    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(product);
    }
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> getProductsBySellerId(@PathVariable Long sellerId) {
        List<Product> products = productService.getAllProductsBySellerId(sellerId);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(products);
    }

    // Create a new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Get the authenticated user (seller)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get the email of the authenticated user
        User seller = userService.findByEmail(email); // Find the user by email

        if (seller == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return forbidden if no seller found
        }

        // Set the seller of the product
        product.setSeller(seller);

        // Optionally set the buyer to null or leave it unset
        product.setBuyer(null);

        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(updated);
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }
}
