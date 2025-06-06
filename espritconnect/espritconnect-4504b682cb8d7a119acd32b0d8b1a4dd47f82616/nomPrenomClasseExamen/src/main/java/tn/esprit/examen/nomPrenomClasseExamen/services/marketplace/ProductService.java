package tn.esprit.examen.nomPrenomClasseExamen.services.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.dto.ProductDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.marketplace.Product;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.marketplace.ProductRepository;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.services.CloudinaryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();

        for (Product product : products) {
            ProductDTO dto = new ProductDTO(
                    product.getIdProduct(),
                    product.getName(),
                    product.getCategory().name(),  // assuming you want to convert enum to String
                    product.getAskingPrice(),
                    product.getDescription(),
                    product.getDeadline(),
                    product.isItemState(),
                    product.getSellerName(),  // This will get the seller's name
                    product.getBuyerName(),   // This will get the buyer's name
                    product.getImage()
            );
            productDTOs.add(dto);
        }
        return productDTOs;
    }

    public List<Product> getAllProductsBySellerId(Long sellerId) {
        List<Product> products = productRepository.findProductsBySellerId(sellerId);
        return products;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product createProduct(Product product, MultipartFile image) {
        try {
            // Upload the image to Cloudinary and get the URL
            String imageUrl = cloudinaryService.uploadFile(image, "product");
            product.setImage(imageUrl); // Set the image URL to the product

            // Save the product with the image URL
            return productRepository.save(product);
        } catch (Exception e) {
            // Handle errors, e.g., if image upload fails
            throw new RuntimeException("Image upload failed", e);
        }
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            // Update only the fields that are included in the updatedProduct
            product.setName(updatedProduct.getName());
            product.setCategory(updatedProduct.getCategory());
            product.setAskingPrice(updatedProduct.getAskingPrice());
            product.setDescription(updatedProduct.getDescription());
            product.setDeadline(updatedProduct.getDeadline());
            product.setItemState(updatedProduct.isItemState());
            product.setBuyer(updatedProduct.getBuyer());  // Optional if buyer is being updated
            product.setImage(updatedProduct.getImage());
            product.setUpdatedAt(LocalDateTime.now());  // Set the updated timestamp
            return productRepository.save(product);
        }
        return null;
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
