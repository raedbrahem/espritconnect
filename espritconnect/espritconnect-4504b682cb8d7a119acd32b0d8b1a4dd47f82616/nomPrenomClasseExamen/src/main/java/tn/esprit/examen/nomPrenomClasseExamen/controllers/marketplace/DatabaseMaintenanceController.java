package tn.esprit.examen.nomPrenomClasseExamen.controllers.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance")
public class DatabaseMaintenanceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Fix the foreign key constraint for payments table
     * @return Success message
     */
    @PostMapping("/fix-payment-constraint")
    public ResponseEntity<String> fixPaymentConstraint() {
        try {
            // Drop the existing foreign key constraint
            jdbcTemplate.execute("ALTER TABLE payments DROP FOREIGN KEY FK81gagumt0r8y3rmudcgpbk42l");

            // Recreate the constraint with ON DELETE CASCADE
            jdbcTemplate.execute("ALTER TABLE payments " +
                    "ADD CONSTRAINT FK81gagumt0r8y3rmudcgpbk42l " +
                    "FOREIGN KEY (order_id) " +
                    "REFERENCES orders(id_order) " +
                    "ON DELETE CASCADE");

            return ResponseEntity.ok("Payment constraint fixed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fixing constraint: " + e.getMessage());
        }
    }
}
