import sys
import cv2
import numpy as np
import os
import logging
from PIL import Image
import io

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('enhanced_categorizer')

# Custom category mapping from COCO classes to our application categories
CATEGORY_MAPPING = {
    # Electronics
    'CELL PHONE': 'Electronics',
    'LAPTOP': 'Electronics',
    'KEYBOARD': 'Electronics',
    'CAMERA': 'Electronics',
    'MOUSE': 'Electronics',
    'COMPUTER': 'Electronics',
    'TABLET': 'Electronics',
    'HEADPHONES': 'Electronics',
    'SPEAKER': 'Electronics',
    'PHONE': 'Electronics',
    'SMARTPHONE': 'Electronics',
    'APPLE': 'Electronics',  # Often detects Apple products
    'DEVICE': 'Electronics',
    'GADGET': 'Electronics',
    'CHARGER': 'Electronics',
    'CABLE': 'Electronics',
    'POWER BANK': 'Electronics',
    'BATTERY': 'Electronics',


    # Bags
    'BACKPACK': 'Bags',
    'HANDBAG': 'Bags',
    'BAG': 'Bags',
    'LUGGAGE': 'Bags',
    'PURSE': 'Bags',


    # Accessories
    'WATCH': 'Accessories',
    'GLASSES': 'Accessories',
    'NECKLACE': 'Accessories',
    'RING': 'Accessories',
    'BRACELET': 'Accessories',
    'EARRINGS': 'Accessories',
    'JEWELRY': 'Accessories',



    # Keys
    'KEY': 'Keys',
    'SCISSORS': 'Keys',  # Often confused with keys
    'KNIFE': 'Keys',     # Similar metallic objects


    # Documents
    'BOOK': 'Documents',
    'NOTEBOOK': 'Documents',
    'PAPER': 'Documents',
    'DOCUMENT': 'Documents',
    'CARD': 'Documents',




    # Default fallback
    'PERSON': 'Other',
    'BICYCLE': 'Other',
    'CAR': 'Other',
    'MOTORCYCLE': 'Other',
    'AIRPLANE': 'Other',
    'BUS': 'Other',
    'TRAIN': 'Other',
    'TRUCK': 'Other',
    'BOAT': 'Other',
}

def preprocess_image(image):
    """Preprocess the image to improve detection"""
    # Convert to RGB if it's not
    if len(image.shape) == 2:
        image = cv2.cvtColor(image, cv2.COLOR_GRAY2RGB)
    elif image.shape[2] == 4:
        image = cv2.cvtColor(image, cv2.COLOR_RGBA2RGB)

    # Resize to a reasonable size if too large
    max_dim = 1024
    h, w = image.shape[:2]
    if max(h, w) > max_dim:
        if h > w:
            new_h, new_w = max_dim, int(w * max_dim / h)
        else:
            new_h, new_w = int(h * max_dim / w), max_dim
        image = cv2.resize(image, (new_w, new_h))

    # Apply some light preprocessing
    image = cv2.GaussianBlur(image, (3, 3), 0)
    image = cv2.convertScaleAbs(image, alpha=1.1, beta=10)  # Increase contrast slightly

    return image

def predict_category_with_confidence(image_bytes, confidence_threshold=0.3):
    """Predict the category of an image using YOLO with confidence score"""
    try:
        # Load YOLO model
        weights_path = "yolov3.weights"
        config_path = "yolov3.cfg"
        classes_path = "coco.names"

        # Check if model files exist
        if not os.path.exists(weights_path) or not os.path.exists(config_path) or not os.path.exists(classes_path):
            logger.error(f"Missing YOLO model files. Please ensure {weights_path}, {config_path}, and {classes_path} exist.")
            return "Other", 0.0

        net = cv2.dnn.readNet(weights_path, config_path)
        with open(classes_path, "r") as f:
            classes = [line.strip().upper() for line in f.readlines()]

        # Convert bytes to image
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            logger.error("Cannot decode image from bytes")
            return "Other", 0.0

        # Preprocess the image
        img = preprocess_image(img)

        # Prepare image for YOLO
        height, width, _ = img.shape
        blob = cv2.dnn.blobFromImage(img, 1/255.0, (416, 416), swapRB=True, crop=False)
        net.setInput(blob)

        # Get output layer names
        layer_names = net.getLayerNames()
        try:
            # OpenCV 4.5.4+
            output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers().flatten()]
        except:
            # Older OpenCV versions
            output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]

        # Run forward pass
        outs = net.forward(output_layers)

        # Process detections
        class_ids = []
        confidences = []
        boxes = []

        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]

                if confidence > confidence_threshold:
                    # Object detected
                    center_x = int(detection[0] * width)
                    center_y = int(detection[1] * height)
                    w = int(detection[2] * width)
                    h = int(detection[3] * height)

                    # Rectangle coordinates
                    x = int(center_x - w / 2)
                    y = int(center_y - h / 2)

                    boxes.append([x, y, w, h])
                    confidences.append(float(confidence))
                    class_ids.append(class_id)

        # Apply non-maximum suppression to remove overlapping boxes
        indices = cv2.dnn.NMSBoxes(boxes, confidences, confidence_threshold, 0.4)

        # Prepare results
        results = []
        if len(indices) > 0:
            for i in indices.flatten():
                class_id = class_ids[i]
                confidence = confidences[i]
                label = classes[class_id] if class_id < len(classes) else "UNKNOWN"
                results.append((label, confidence))

        # If no results, return Other
        if not results:
            logger.info("No objects detected with sufficient confidence")
            return "Other", 0.0

        # Sort by confidence (highest first)
        results.sort(key=lambda x: x[1], reverse=True)

        # Get the highest confidence detection
        best_label, best_confidence = results[0]

        # Map to our application categories
        mapped_category = CATEGORY_MAPPING.get(best_label, "Other")

        logger.info(f"Detected {best_label} with confidence {best_confidence:.2f}, mapped to {mapped_category}")

        # Log all detections for debugging
        logger.info(f"All detections: {results}")

        return mapped_category, best_confidence

    except Exception as e:
        logger.error(f"Error in category prediction: {str(e)}")
        return "Other", 0.0

def analyze_image_colors(image_bytes):
    """Analyze image colors to help with categorization"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return None

        # Convert to HSV for better color analysis
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

        # Calculate color histograms
        h_hist = cv2.calcHist([hsv], [0], None, [180], [0, 180])
        s_hist = cv2.calcHist([hsv], [1], None, [256], [0, 256])
        v_hist = cv2.calcHist([hsv], [2], None, [256], [0, 256])

        # Normalize histograms
        h_hist = cv2.normalize(h_hist, h_hist, 0, 1, cv2.NORM_MINMAX)
        s_hist = cv2.normalize(s_hist, s_hist, 0, 1, cv2.NORM_MINMAX)
        v_hist = cv2.normalize(v_hist, v_hist, 0, 1, cv2.NORM_MINMAX)

        # Find dominant hue
        max_h_val = np.max(h_hist)
        max_h_idx = np.where(h_hist == max_h_val)[0][0]

        # Find dominant saturation
        max_s_val = np.max(s_hist)
        max_s_idx = np.where(s_hist == max_s_val)[0][0]

        # Find dominant value (brightness)
        max_v_val = np.max(v_hist)
        max_v_idx = np.where(v_hist == max_v_val)[0][0]

        # Analyze dominant colors
        color_info = {
            'dominant_hue': max_h_idx,
            'dominant_saturation': max_s_idx,
            'dominant_brightness': max_v_idx,
            'avg_saturation': np.mean(hsv[:,:,1]),
            'avg_brightness': np.mean(hsv[:,:,2])
        }

        return color_info

    except Exception as e:
        logger.error(f"Error in color analysis: {str(e)}")
        return None

def analyze_image_shape(image_bytes):
    """Analyze image shape to help with categorization"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return None

        # Convert to grayscale for shape analysis
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Apply threshold
        _, thresh = cv2.threshold(gray, 127, 255, cv2.THRESH_BINARY)

        # Find contours
        contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        # Analyze largest contour
        if contours:
            largest_contour = max(contours, key=cv2.contourArea)
            area = cv2.contourArea(largest_contour)
            perimeter = cv2.arcLength(largest_contour, True)

            # Calculate shape metrics
            if perimeter > 0:
                circularity = 4 * np.pi * area / (perimeter * perimeter)

                # Get bounding rectangle
                _, _, w, h = cv2.boundingRect(largest_contour)
                aspect_ratio = float(w) / h if h > 0 else 0

                return {
                    'area': area,
                    'perimeter': perimeter,
                    'circularity': circularity,
                    'aspect_ratio': aspect_ratio
                }

        return None

    except Exception as e:
        logger.error(f"Error in shape analysis: {str(e)}")
        return None

def analyze_image_texture(image_bytes):
    """Analyze image texture to help with categorization"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return None

        # Convert to grayscale
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Calculate texture features
        # 1. GLCM (Gray-Level Co-occurrence Matrix) features
        # For simplicity, we'll use basic statistical measures

        # Calculate gradient magnitude
        sobelx = cv2.Sobel(gray, cv2.CV_64F, 1, 0, ksize=3)
        sobely = cv2.Sobel(gray, cv2.CV_64F, 0, 1, ksize=3)
        gradient_magnitude = np.sqrt(sobelx**2 + sobely**2)

        # Calculate texture metrics
        texture_mean = np.mean(gradient_magnitude)
        texture_std = np.std(gradient_magnitude)
        texture_energy = np.sum(gradient_magnitude**2)

        # Calculate edge density (useful for keys)
        _, binary = cv2.threshold(gray, 127, 255, cv2.THRESH_BINARY)
        edges = cv2.Canny(binary, 100, 200)
        edge_density = np.sum(edges > 0) / (edges.shape[0] * edges.shape[1])

        return {
            'texture_mean': texture_mean,
            'texture_std': texture_std,
            'texture_energy': texture_energy,
            'edge_density': edge_density
        }

    except Exception as e:
        logger.error(f"Error in texture analysis: {str(e)}")
        return None

def detect_fabric_texture(image_bytes):
    """Detect fabric-like textures in an image to identify clothing"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return 0.0

        # Convert to grayscale
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Apply Gaussian blur to reduce noise
        blurred = cv2.GaussianBlur(gray, (3, 3), 0)

        # Calculate texture features using different methods
        fabric_score = 0.0

        # 1. Look for repeating patterns using FFT
        # Apply FFT to detect repeating patterns (common in fabrics)
        f_transform = np.fft.fft2(blurred)
        f_shift = np.fft.fftshift(f_transform)
        magnitude_spectrum = 20 * np.log(np.abs(f_shift) + 1)

        # Check for peaks in the frequency domain (repeating patterns)
        # Exclude the DC component (center of the spectrum)
        h, w = magnitude_spectrum.shape
        center_h, center_w = h // 2, w // 2
        mask = np.ones_like(magnitude_spectrum, dtype=bool)
        mask[center_h-10:center_h+10, center_w-10:center_w+10] = False

        # Find peaks in the frequency domain
        threshold = np.percentile(magnitude_spectrum[mask], 95)
        peaks = np.sum(magnitude_spectrum > threshold)

        # Normalize by image size
        peak_density = peaks / (h * w)

        # Fabrics often have regular patterns
        if 0.001 < peak_density < 0.01:
            fabric_score += 0.3

        # 2. Check for texture homogeneity (fabrics tend to have consistent texture)
        # Calculate local binary pattern (simplified version)
        texture_blocks = []
        block_size = 16
        for y in range(0, h-block_size, block_size):
            for x in range(0, w-block_size, block_size):
                block = blurred[y:y+block_size, x:x+block_size]
                # Calculate gradient magnitude as a simple texture measure
                gx = cv2.Sobel(block, cv2.CV_32F, 1, 0)
                gy = cv2.Sobel(block, cv2.CV_32F, 0, 1)
                mag = np.sqrt(gx*gx + gy*gy)
                texture_blocks.append(np.mean(mag))

        if len(texture_blocks) > 4:
            # Calculate coefficient of variation (lower means more uniform texture)
            texture_std = np.std(texture_blocks)
            texture_mean = np.mean(texture_blocks)
            if texture_mean > 0:
                texture_cv = texture_std / texture_mean

                # Fabrics usually have consistent texture (low CV)
                if texture_cv < 0.3:
                    fabric_score += 0.3
                elif texture_cv < 0.5:
                    fabric_score += 0.2

        # 3. Check for fabric-like edges (soft, flowing)
        edges = cv2.Canny(blurred, 50, 150)
        contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

        # Clothing often has curved contours
        curved_contours = 0
        for contour in contours:
            if len(contour) > 20:  # Only consider longer contours
                # Calculate contour curvature
                perimeter = cv2.arcLength(contour, True)
                area = cv2.contourArea(contour)
                if perimeter > 0:
                    # Circularity measure
                    circularity = 4 * np.pi * area / (perimeter * perimeter)
                    # Clothing often has medium circularity (not too straight, not too circular)
                    if 0.1 < circularity < 0.7:
                        curved_contours += 1

        # If we have several curved contours, it might be clothing
        if curved_contours >= 3:
            fabric_score += 0.3
        elif curved_contours >= 1:
            fabric_score += 0.1

        # Normalize score
        fabric_score = min(fabric_score, 1.0)

        logger.info(f"Fabric texture detection score: {fabric_score:.2f}")
        return fabric_score

    except Exception as e:
        logger.error(f"Error in fabric texture detection: {str(e)}")
        return 0.0

def detect_text_like_features(image_bytes):
    """Detect text-like features in an image to identify documents"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return 0.0

        # Convert to grayscale
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Apply Gaussian blur to reduce noise
        blurred = cv2.GaussianBlur(gray, (5, 5), 0)

        # Apply adaptive thresholding to detect text-like features
        thresh = cv2.adaptiveThreshold(blurred, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                      cv2.THRESH_BINARY_INV, 11, 2)

        # Find contours
        contours, _ = cv2.findContours(thresh, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

        # Text-like features: many small contours with similar heights
        text_score = 0.0

        # Filter contours by size
        valid_contours = []
        heights = []

        for contour in contours:
            x, y, w, h = cv2.boundingRect(contour)
            area = cv2.contourArea(contour)

            # Text characters are usually small but not too small
            if 10 < area < 500 and 5 < h < 50:
                valid_contours.append(contour)
                heights.append(h)

        # Check if we have enough potential text contours
        if len(valid_contours) > 10:
            # Calculate standard deviation of heights (text lines have similar heights)
            if len(heights) > 0:
                height_std = np.std(heights)
                height_mean = np.mean(heights)

                # Text has consistent height and many contours
                if height_std / height_mean < 0.5:
                    text_score += 0.3

                # Check for horizontal alignment (text lines)
                y_coords = [cv2.boundingRect(c)[1] for c in valid_contours]
                y_clusters = {}

                # Group contours by similar y-coordinates (text lines)
                for y in y_coords:
                    for base_y in range(y-5, y+6):
                        if base_y in y_clusters:
                            y_clusters[base_y] += 1
                            break
                    else:
                        y_clusters[y] = 1

                # If we have several horizontal lines with multiple contours, it's likely text
                text_lines = sum(1 for count in y_clusters.values() if count >= 3)
                if text_lines >= 3:
                    text_score += 0.4
                elif text_lines >= 1:
                    text_score += 0.2

                # Check for regular spacing (like text)
                x_coords = sorted([cv2.boundingRect(c)[0] for c in valid_contours])
                if len(x_coords) > 5:
                    diffs = [x_coords[i+1] - x_coords[i] for i in range(len(x_coords)-1)]
                    if len(diffs) > 0:
                        spacing_std = np.std(diffs)
                        spacing_mean = np.mean(diffs)
                        if spacing_mean > 0 and spacing_std / spacing_mean < 0.7:
                            text_score += 0.3

        # Normalize score
        text_score = min(text_score, 1.0)

        logger.info(f"Text detection score: {text_score:.2f}")
        return text_score

    except Exception as e:
        logger.error(f"Error in text detection: {str(e)}")
        return 0.0

def detect_keys(image_bytes):
    """Specialized function to detect keys in images"""
    try:
        if isinstance(image_bytes, io.BytesIO):
            np_arr = np.frombuffer(image_bytes.getvalue(), np.uint8)
        else:
            np_arr = np.frombuffer(image_bytes, np.uint8)

        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return 0.0

        # Convert to grayscale
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Apply Gaussian blur to reduce noise
        blurred = cv2.GaussianBlur(gray, (5, 5), 0)

        # Apply adaptive thresholding
        thresh = cv2.adaptiveThreshold(blurred, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                      cv2.THRESH_BINARY_INV, 11, 2)

        # Find contours
        contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        # Key-like features
        key_score = 0.0

        for contour in contours:
            # Calculate contour properties
            area = cv2.contourArea(contour)
            if area < 100:  # Skip very small contours
                continue

            perimeter = cv2.arcLength(contour, True)
            if perimeter == 0:
                continue

            # Calculate shape descriptors
            circularity = 4 * np.pi * area / (perimeter * perimeter)

            # Get bounding rectangle
            x, y, w, h = cv2.boundingRect(contour)
            aspect_ratio = float(w) / h if h > 0 else 0

            # Key-like shape: elongated with teeth-like features
            # Much more restrictive criteria
            if (aspect_ratio > 3.0 or aspect_ratio < 0.33) and circularity < 0.4:
                # Check for teeth-like features (small variations along the contour)
                hull = cv2.convexHull(contour)
                hull_area = cv2.contourArea(hull)
                if hull_area > 0:
                    solidity = float(area) / hull_area
                    # More restrictive solidity threshold
                    if solidity < 0.8 and solidity > 0.4:  # Non-convex shape (like keys with teeth)
                        # Check if the contour has a key-like shape (long thin part with wider head)
                        # Get the extreme points
                        leftmost = tuple(contour[contour[:,:,0].argmin()][0])
                        rightmost = tuple(contour[contour[:,:,0].argmax()][0])
                        topmost = tuple(contour[contour[:,:,1].argmin()][0])
                        bottommost = tuple(contour[contour[:,:,1].argmax()][0])

                        # Calculate distances between extreme points
                        width = rightmost[0] - leftmost[0]
                        height = bottommost[1] - topmost[1]

                        # Keys typically have a distinctive shape
                        if width > 0 and height > 0 and (width/height > 2.5 or height/width > 2.5):
                            key_score += 0.15  # Lower score per detection

        # Normalize score
        key_score = min(key_score, 1.0)

        logger.info(f"Key detection score: {key_score:.2f}")
        return key_score

    except Exception as e:
        logger.error(f"Error in key detection: {str(e)}")
        return 0.0

def enhanced_category_prediction(image_bytes):
    """Enhanced category prediction using multiple techniques"""
    # First try YOLO detection as it's more reliable for general categories
    category, confidence = predict_category_with_confidence(image_bytes)

    # If YOLO gives high confidence, trust it
    if confidence > 0.6 and category != "Other":
        logger.info(f"High confidence YOLO detection: {category} with {confidence:.2f}")
        return category, confidence

    # For moderate confidence, be more careful with certain categories
    if confidence > 0.4 and category != "Other":
        # Be skeptical of Keys and Documents from YOLO
        if category == "Keys" or category == "Documents":
            # Require additional confirmation
            if category == "Keys":
                # Check if it really looks like a key
                key_score = detect_keys(image_bytes)
                if key_score > 0.6:
                    logger.info(f"YOLO and specialized detection confirm Keys")
                    return "Keys", 0.7
                else:
                    # Default to Electronics for unconfirmed keys
                    logger.info(f"YOLO suggests Keys but not confirmed by specialized detection")
                    return "Electronics", 0.5
            elif category == "Documents":
                # Check if it really looks like a document
                text_score = detect_text_like_features(image_bytes)
                if text_score > 0.5:
                    logger.info(f"YOLO and specialized detection confirm Documents")
                    return "Documents", 0.7
                else:
                    # Default to Other for unconfirmed documents
                    logger.info(f"YOLO suggests Documents but not confirmed by specialized detection")
                    return "Other", 0.4
        else:
            # For other categories, trust YOLO with moderate confidence
            logger.info(f"Moderate confidence YOLO detection: {category} with {confidence:.2f}")
            return category, confidence

    # If YOLO is uncertain, use a balanced approach with all specialized detectors
    # Get scores from all specialized detectors
    key_score = detect_keys(image_bytes) * 0.7  # Apply penalty to reduce key false positives
    text_score = detect_text_like_features(image_bytes) * 0.8  # Apply penalty to reduce document false positives
    fabric_score = detect_fabric_texture(image_bytes)

    # Use a balanced approach with all specialized detectors
    # Check if any specialized detector has very high confidence
    if key_score > 0.8:
        logger.info(f"Very high confidence key detection: {key_score:.2f}")
        return "Keys", 0.8
    elif text_score > 0.8:
        logger.info(f"Very high confidence document detection: {text_score:.2f}")
        return "Documents", 0.8
    elif fabric_score > 0.8:
        logger.info(f"Very high confidence clothing detection: {fabric_score:.2f}")
        return "Clothing", 0.8

    # Then try YOLO detection
    category, confidence = predict_category_with_confidence(image_bytes)

    # If confidence is too low or category is Other, try additional analysis
    if confidence < 0.5 or category == "Other":
        logger.info(f"Low confidence detection ({confidence:.2f}) or 'Other' category, trying additional analysis")

        # Try color analysis
        color_info = analyze_image_colors(image_bytes)
        shape_info = analyze_image_shape(image_bytes)
        texture_info = analyze_image_texture(image_bytes)

        # Combine all analyses for better prediction
        if color_info:
            hue = color_info['dominant_hue']
            saturation = color_info['avg_saturation']
            brightness = color_info['avg_brightness']

            # Color-based heuristics
            # More sophisticated document detection - don't just rely on white background
            # Check for document-like features instead of just white background
            if saturation < 50 and brightness > 180:
                # Don't immediately return - this is just a hint that it *might* be a document
                # We'll check for more document-like features
                document_score = 0.3

                # Check for text-like features using edge detection
                text_score = detect_text_like_features(image_bytes)
                if text_score > 0.5:
                    logger.info(f"Document detection with text features: {text_score:.2f}")
                    return "Documents", 0.7
                elif text_score > 0.3:
                    document_score += 0.2

                # Only return Documents if we're reasonably confident
                if document_score > 0.4:
                    logger.info("Color analysis suggests possible document, but with low confidence")
                    return "Documents", document_score

            # Brown/tan bags or accessories
            if 20 <= hue <= 30 and saturation > 100:
                logger.info("Color analysis suggests Bags based on brown color")
                return "Bags", 0.6

            # Black/dark electronics
            if brightness < 50 and saturation < 30:
                logger.info("Color analysis suggests Electronics based on dark color")
                return "Electronics", 0.55

            # Metallic objects - could be electronics, accessories, etc.
            if ((15 <= hue <= 35) and 30 <= saturation <= 90 and 100 <= brightness <= 180):
                # Metallic color suggests electronics or accessories, not keys
                logger.info("Metallic color detected - likely Electronics or Accessories")

                # Check for electronics-like features (smooth texture)
                if texture_info and texture_info['texture_std'] < 25:
                    logger.info("Metallic color with smooth texture suggests Electronics")
                    return "Electronics", 0.65
                else:
                    logger.info("Metallic color suggests Accessories")
                    return "Accessories", 0.6

            # Don't rely solely on color for clothing detection
            # Just note if the color is typical of clothing
            clothing_score = 0.0
            if (saturation > 150 and brightness > 100) or (20 <= hue <= 40 and saturation > 80):
                clothing_score = 0.3
                logger.info("Color suggests possible clothing, checking other features")

                # Check for clothing-specific features
                fabric_score = detect_fabric_texture(image_bytes)
                if fabric_score > 0.6:
                    logger.info(f"Fabric texture detected with high confidence: {fabric_score:.2f}")
                    return "Clothing", 0.7
                elif fabric_score > 0.4:
                    clothing_score += 0.3

                # Only return Clothing if we're reasonably confident
                if clothing_score > 0.5:
                    logger.info("Multiple features suggest clothing")
                    return "Clothing", clothing_score

        # Shape-based heuristics
        if shape_info:
            # Rectangular documents
            if shape_info['aspect_ratio'] > 1.3 and shape_info['aspect_ratio'] < 1.8 and shape_info['circularity'] < 0.7:
                logger.info("Shape analysis suggests Documents based on rectangular shape")
                return "Documents", 0.6

            # Round accessories
            if shape_info['circularity'] > 0.8:
                logger.info("Shape analysis suggests Accessories based on circular shape")
                return "Accessories", 0.55

            # Long, thin objects - could be various items, not just keys
            if shape_info['aspect_ratio'] > 3.5 and shape_info['area'] < 0.25 * (shape_info['perimeter'] ** 2) / (4 * np.pi):
                # Elongated objects could be pens, tools, electronics accessories, etc.
                logger.info("Elongated shape detected - likely Electronics accessory")
                return "Electronics", 0.6

        # Texture-based heuristics
        if texture_info:
            # Smooth electronics
            if texture_info['texture_std'] < 20:
                logger.info("Texture analysis suggests Electronics based on smooth texture")
                return "Electronics", 0.5

            # High edge density suggests complex objects - could be electronics or accessories
            if texture_info['edge_density'] > 0.2 and texture_info['texture_std'] > 40:
                logger.info("Complex texture detected - likely Electronics or Accessories")

                # Check if it's more likely electronics or accessories
                if 'texture_energy' in texture_info and texture_info['texture_energy'] > 1000000:
                    logger.info("High texture energy suggests Electronics with complex features")
                    return "Electronics", 0.6
                else:
                    logger.info("Moderate texture complexity suggests Accessories")
                    return "Accessories", 0.55



    # If we still have low confidence, use a balanced approach with all analyses
    # We already have the specialized detector scores from above

    # Get color and shape info for additional analysis
    color_info = analyze_image_colors(image_bytes)
    shape_info = analyze_image_shape(image_bytes)

    # Calculate balanced scores for each category
    scores = {
        "Keys": key_score * 0.7,  # Apply penalty to reduce key false positives
        "Documents": text_score * 0.8,  # Apply penalty to reduce document false positives
        "Clothing": fabric_score,
        "Electronics": 0.35,  # Default bias for Electronics
        "Bags": 0.25,       # Default bias for Bags
        "Accessories": 0.25  # Default bias for Accessories
    }

    # Apply additional heuristics based on color and shape
    if color_info:
        hue = color_info['dominant_hue']
        saturation = color_info['avg_saturation']
        brightness = color_info['avg_brightness']

        # Dark objects are likely Electronics
        if brightness < 60 and saturation < 40:
            scores["Electronics"] += 0.2

        # Bright, colorful objects might be Clothing
        if saturation > 150 and brightness > 120:
            scores["Clothing"] += 0.15

        # Metallic colors could be Keys or Electronics
        if 15 <= hue <= 35 and 30 <= saturation <= 90 and 100 <= brightness <= 180:
            scores["Keys"] += 0.1
            scores["Electronics"] += 0.1

    if shape_info:
        # Rectangular objects might be Documents or Electronics
        if 1.3 < shape_info['aspect_ratio'] < 1.8 and shape_info['circularity'] < 0.7:
            scores["Documents"] += 0.1
            scores["Electronics"] += 0.1

        # Round objects are likely Accessories
        if shape_info['circularity'] > 0.8:
            scores["Accessories"] += 0.2

        # Long, thin objects might be Keys
        if shape_info['aspect_ratio'] > 3.0:
            scores["Keys"] += 0.15

    # Find the category with the highest score
    best_category = max(scores.items(), key=lambda x: x[1])

    if best_category[1] > 0.3:  # If we have a reasonable score
        logger.info(f"Using best specialized detector: {best_category[0]} with score {best_category[1]:.2f}")
        return best_category[0], best_category[1]
    else:
        # Default to Electronics as it's a common lost item category
        logger.info("Defaulting to Electronics as a common lost item category")
        return "Electronics", 0.4

# Main function for direct script execution
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python enhanced_categorizer.py <image_path>")
        sys.exit(1)

    image_path = sys.argv[1]

    try:
        # Read image file
        with open(image_path, 'rb') as f:
            image_bytes = f.read()

        # Predict category
        category, confidence = enhanced_category_prediction(image_bytes)

        # Print results in a format that can be parsed by the Java code
        print(f"CATEGORY:{category}")
        print(f"CONFIDENCE:{confidence:.2f}")

    except Exception as e:
        logger.error(f"Error processing image: {str(e)}")
        print("CATEGORY:Other")
        print("CONFIDENCE:0.0")
