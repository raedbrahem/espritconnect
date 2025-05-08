import os
import sys
import cv2
import numpy as np
import logging
from PIL import Image
import io

# Set up detailed logging
logging.basicConfig(level=logging.DEBUG, 
                    format='%(asctime)s - %(levelname)s - %(message)s',
                    handlers=[
                        logging.FileHandler("categorizer_debug.log"),
                        logging.StreamHandler()
                    ])
logger = logging.getLogger('categorizer_diagnostic')

def check_files():
    """Check if all required files exist"""
    required_files = ["yolov3.weights", "yolov3.cfg", "coco.names"]
    missing_files = []
    
    for file in required_files:
        if not os.path.exists(file):
            missing_files.append(file)
    
    if missing_files:
        logger.error(f"Missing required files: {missing_files}")
        return False
    else:
        logger.info("All required files exist")
        # Log file sizes to ensure they're not empty or corrupted
        for file in required_files:
            size = os.path.getsize(file)
            logger.info(f"File {file}: {size} bytes")
        return True

def test_yolo_loading():
    """Test if YOLO model can be loaded"""
    try:
        logger.info("Attempting to load YOLO model...")
        weights_path = "yolov3.weights"
        config_path = "yolov3.cfg"
        
        # Log absolute paths
        abs_weights_path = os.path.abspath(weights_path)
        abs_config_path = os.path.abspath(config_path)
        logger.info(f"Absolute paths: weights={abs_weights_path}, config={abs_config_path}")
        
        # Try to load the model
        net = cv2.dnn.readNet(weights_path, config_path)
        
        # Check if model loaded successfully
        layer_names = net.getLayerNames()
        logger.info(f"Model loaded successfully with {len(layer_names)} layers")
        
        # Try to get output layers
        try:
            # OpenCV 4.5.4+
            output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers().flatten()]
        except:
            # Older OpenCV versions
            output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
        
        logger.info(f"Output layers: {output_layers}")
        return True
        
    except Exception as e:
        logger.error(f"Error loading YOLO model: {str(e)}")
        return False

def test_image_processing(image_path=None):
    """Test image processing with a sample image"""
    try:
        # If no image provided, create a simple test image
        if image_path is None or not os.path.exists(image_path):
            logger.info("Creating test image...")
            # Create a simple test image (red rectangle on white background)
            img = np.ones((416, 416, 3), dtype=np.uint8) * 255
            cv2.rectangle(img, (100, 100), (300, 300), (0, 0, 255), -1)
            
            # Save the test image
            test_image_path = "test_image.jpg"
            cv2.imwrite(test_image_path, img)
            logger.info(f"Created test image: {test_image_path}")
            image_path = test_image_path
        
        # Read the image
        logger.info(f"Reading image from {image_path}")
        img = cv2.imread(image_path)
        
        if img is None:
            logger.error(f"Failed to read image from {image_path}")
            return False
        
        logger.info(f"Image loaded successfully: shape={img.shape}")
        
        # Convert to bytes (simulating the input to the categorizer)
        _, buffer = cv2.imencode('.jpg', img)
        image_bytes = buffer.tobytes()
        
        # Try to run the enhanced_categorizer's prediction function
        try:
            # Import here to avoid circular imports
            from enhanced_categorizer import enhanced_category_prediction
            
            logger.info("Calling enhanced_category_prediction...")
            category, confidence = enhanced_category_prediction(image_bytes)
            
            logger.info(f"Prediction result: category={category}, confidence={confidence}")
            return True
            
        except Exception as e:
            logger.error(f"Error in enhanced_category_prediction: {str(e)}")
            import traceback
            logger.error(traceback.format_exc())
            return False
        
    except Exception as e:
        logger.error(f"Error in test_image_processing: {str(e)}")
        import traceback
        logger.error(traceback.format_exc())
        return False

def main():
    logger.info("Starting categorizer diagnostic...")
    logger.info(f"Current working directory: {os.getcwd()}")
    logger.info(f"Python executable: {sys.executable}")
    logger.info(f"Python version: {sys.version}")
    logger.info(f"OpenCV version: {cv2.__version__}")
    
    # Check if required files exist
    if not check_files():
        logger.error("Required files are missing. Please download them first.")
        return
    
    # Test YOLO model loading
    if not test_yolo_loading():
        logger.error("Failed to load YOLO model.")
        return
    
    # Test image processing
    if not test_image_processing():
        logger.error("Failed to process test image.")
        return
    
    logger.info("Diagnostic completed successfully.")

if __name__ == "__main__":
    main()
