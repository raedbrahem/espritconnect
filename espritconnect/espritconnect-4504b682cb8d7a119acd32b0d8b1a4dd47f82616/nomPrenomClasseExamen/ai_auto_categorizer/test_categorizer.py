import sys
import os
import logging
from enhanced_categorizer import enhanced_category_prediction

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('test_categorizer')

def test_categorizer(image_path):
    """Test the enhanced categorizer with a specific image"""
    logger.info(f"Testing categorizer with image: {image_path}")
    
    # Check if the image exists
    if not os.path.exists(image_path):
        logger.error(f"Image file does not exist: {image_path}")
        return
    
    try:
        # Read the image file
        with open(image_path, 'rb') as f:
            image_bytes = f.read()
        
        # Run the categorizer
        category, confidence = enhanced_category_prediction(image_bytes)
        
        # Print results in the format expected by the Java code
        print(f"CATEGORY:{category}")
        print(f"CONFIDENCE:{confidence:.2f}")
        
        # Also print in a more readable format for manual testing
        logger.info(f"Predicted category: {category} with confidence {confidence:.2f}")
        
    except Exception as e:
        logger.error(f"Error testing categorizer: {str(e)}")
        import traceback
        logger.error(traceback.format_exc())

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python test_categorizer.py <image_path>")
        sys.exit(1)
    
    image_path = sys.argv[1]
    test_categorizer(image_path)
