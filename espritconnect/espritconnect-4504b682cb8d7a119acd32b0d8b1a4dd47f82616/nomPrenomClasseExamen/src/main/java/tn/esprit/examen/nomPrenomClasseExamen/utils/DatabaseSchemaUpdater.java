package tn.esprit.examen.nomPrenomClasseExamen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to execute SQL scripts for database schema updates
 * This runs once at application startup
 */
@Component
public class DatabaseSchemaUpdater implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaUpdater.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Executing database schema updates...");
            
            // Load and execute the SQL script
            String sql = readResourceFile("db/update_category_column.sql");
            logger.info("Executing SQL: {}", sql);
            
            jdbcTemplate.execute(sql);
            logger.info("Database schema updated successfully");
        } catch (Exception e) {
            logger.error("Error updating database schema", e);
            // Don't fail the application startup if the script fails
            // The error is logged but the application will continue to start
        }
    }

    /**
     * Read a resource file as a string
     */
    private String readResourceFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
