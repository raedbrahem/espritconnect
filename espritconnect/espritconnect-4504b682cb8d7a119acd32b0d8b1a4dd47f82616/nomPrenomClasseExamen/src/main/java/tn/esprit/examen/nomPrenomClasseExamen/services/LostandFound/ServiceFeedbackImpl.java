package tn.esprit.examen.nomPrenomClasseExamen.services.LostandFound;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.CategoryItem;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Feedback;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LostandFound.Item;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.FeedbackRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.LostandFound.LostandFoundRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ServiceFeedbackImpl {
    private static final Logger logger = LoggerFactory.getLogger(ServiceFeedbackImpl.class);

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private LostandFoundRepository itemRepository;

    /**
     * Save feedback and update item category if needed
     *
     * @param feedback The feedback to save
     * @return The saved feedback
     */
    public Feedback saveFeedback(Feedback feedback) {
        logger.info("Saving feedback for item ID: {}", feedback.getItem().getId_item());

        // Save the feedback
        Feedback savedFeedback = feedbackRepository.save(feedback);

        // If the category is incorrect, update the item with the explanation as the new category
        if (!feedback.isCorrectCategory() && feedback.getExplanation() != null && !feedback.getExplanation().trim().isEmpty()) {
            Item item = feedback.getItem();
            String newCategory = feedback.getExplanation().trim();
            String oldCategory = item.getCategory() != null ? item.getCategory().toString() : "undefined";

            logger.info("Updating item ID: {} category from '{}' to '{}'",
                    item.getId_item(), oldCategory, newCategory);

            try {
                // Convert the string to a CategoryItem enum
                CategoryItem categoryEnum;
                try {
                    categoryEnum = CategoryItem.valueOf(newCategory.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid category name: {}. Must be one of: {}",
                            newCategory, java.util.Arrays.toString(CategoryItem.values()));
                    // Continue without updating the category
                    return savedFeedback;
                }

                // Use direct update query instead of saving the whole entity
                int updated = itemRepository.updateCategory(item.getId_item(), categoryEnum);
                if (updated > 0) {
                    logger.info("Successfully updated category for item ID: {} to {}", item.getId_item(), categoryEnum);
                } else {
                    logger.warn("No rows affected when updating category for item ID: {}", item.getId_item());
                }
            } catch (Exception e) {
                logger.error("Error updating category for item ID: {}: {}", item.getId_item(), e.getMessage());
                // Continue without failing the whole operation
                // The feedback is still saved even if the category update fails
            }
        }

        return savedFeedback;
    }

    /**
     * Get all feedback for an item
     *
     * @param itemId The item ID
     * @return List of feedback for the item
     */
    public List<Feedback> getFeedbackForItem(Long itemId) {
        return feedbackRepository.findByItemId(itemId);
    }

    /**
     * Get all feedback
     *
     * @return List of all feedback
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}