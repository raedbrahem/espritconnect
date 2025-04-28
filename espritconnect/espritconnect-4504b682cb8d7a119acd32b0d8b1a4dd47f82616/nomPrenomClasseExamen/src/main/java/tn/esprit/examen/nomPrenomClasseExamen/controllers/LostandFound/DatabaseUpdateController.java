package tn.esprit.examen.nomPrenomClasseExamen.controllers.LostandFound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for manual database updates
 * This is useful for executing schema updates without restarting the application
 */
@RestController
@RequestMapping("/api/admin/database")
public class DatabaseUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdateController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Update the category column in the item table
     */
    @PostMapping("/update-category-column")
    public ResponseEntity<?> updateCategoryColumn() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Manually updating category column...");
            
            // Execute the SQL to update the category column
            jdbcTemplate.execute("ALTER TABLE item MODIFY COLUMN category VARCHAR(20)");
            
            logger.info("Category column updated successfully");
            response.put("success", true);
            response.put("message", "Category column updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating category column", e);
            response.put("success", false);
            response.put("message", "Error updating category column: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
