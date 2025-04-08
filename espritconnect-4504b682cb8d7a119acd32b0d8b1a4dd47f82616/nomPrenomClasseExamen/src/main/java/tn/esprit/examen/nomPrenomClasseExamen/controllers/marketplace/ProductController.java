package tn.esprit.examen.nomPrenomClasseExamen.controllers.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.dto.ProductDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.CategoryType;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.services.CloudinaryService;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.ProductService;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private CloudinaryService cloudinaryService;
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

    // POST endpoint to create a product
    @PreAuthorize("hasRole('USER')")

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("askingPrice") BigDecimal askingPrice,
            @RequestParam("description") String description,
            @RequestParam("deadline") String deadlineStr, // <- note this change
            @RequestParam("itemState") boolean itemState,
            @RequestParam("image") MultipartFile image) {

        // 1. Get the authenticated user (seller)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        if (seller == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 2. Convert String to LocalDateTime
        LocalDateTime deadline;
        try {
            Instant instant = Instant.parse(deadlineStr); // handles ISO 8601 with Z
            deadline = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build(); // if invalid date format
        }

        // 3. Create product object
        Product product = new Product();
        product.setName(name);
        product.setCategory(CategoryType.valueOf(category));
        product.setAskingPrice(askingPrice);
        product.setDescription(description);
        product.setDeadline(deadline); // <- now safely used
        product.setItemState(itemState);
        product.setSeller(seller);
        product.setBuyer(null);

        // 4. Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadFile(image, "product");
        System.out.println("Image uploaded, URL: " + imageUrl);
        product.setImage(imageUrl);

        // 5. Save product
        Product createdProduct = productService.createProduct(product, image);
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
