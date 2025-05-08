import sys
import cv2
import numpy as np
import os
from PIL import Image
import imagehash
import requests
from io import BytesIO
import logging
import time
import json

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('improved_matcher')

def download_image_from_url(url):
    """Download an image from a URL and return both PIL image and bytes"""
    try:
        logger.info(f"Downloading image from URL: {url}")
        response = requests.get(url, timeout=10)
        response.raise_for_status()  # Raise exception for HTTP errors

        img_bytes = BytesIO(response.content)
        img = Image.open(img_bytes)

        # Convert to RGB if image is in RGBA mode (has transparency)
        if img.mode == 'RGBA':
            img = img.convert('RGB')

        logger.info(f"Successfully downloaded image: {img.size} pixels")
        return img, BytesIO(response.content)
    except requests.exceptions.RequestException as e:
        logger.error(f"Error downloading image: {str(e)}")
        return None, None
    except Exception as e:
        logger.error(f"Unexpected error processing image: {str(e)}")
        return None, None

def compute_phash(image):
    """Compute perceptual hash for an image"""
    try:
        return imagehash.phash(image)
    except Exception as e:
        logger.error(f"Error computing phash: {str(e)}")
        return None

def compute_color_histogram(image_pil):
    """Compute color histogram for an image"""
    try:
        # Convert PIL image to OpenCV format
        img = np.array(image_pil)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

        # Convert to HSV color space
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

        # Compute histograms for each channel
        h_hist = cv2.calcHist([hsv], [0], None, [30], [0, 180])
        s_hist = cv2.calcHist([hsv], [1], None, [32], [0, 256])
        v_hist = cv2.calcHist([hsv], [2], None, [32], [0, 256])

        # Normalize histograms
        h_hist = cv2.normalize(h_hist, h_hist, 0, 1, cv2.NORM_MINMAX)
        s_hist = cv2.normalize(s_hist, s_hist, 0, 1, cv2.NORM_MINMAX)
        v_hist = cv2.normalize(v_hist, v_hist, 0, 1, cv2.NORM_MINMAX)

        return h_hist, s_hist, v_hist
    except Exception as e:
        logger.error(f"Error computing color histogram: {str(e)}")
        return None, None, None

def compare_histograms(hist1, hist2):
    """Compare two histograms using correlation method"""
    try:
        if hist1 is None or hist2 is None:
            return 0

        # Compare histograms using correlation method (higher is better)
        score = cv2.compareHist(hist1, hist2, cv2.HISTCMP_CORREL)
        return max(0, score)  # Ensure non-negative
    except Exception as e:
        logger.error(f"Error comparing histograms: {str(e)}")
        return 0

def compute_sift_features(image_pil):
    """Compute SIFT features for an image"""
    try:
        # Convert PIL image to OpenCV format
        img = np.array(image_pil)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

        # Initialize SIFT detector
        sift = cv2.SIFT_create()

        # Detect keypoints and compute descriptors
        keypoints, descriptors = sift.detectAndCompute(img, None)

        return keypoints, descriptors
    except Exception as e:
        logger.error(f"Error computing SIFT features: {str(e)}")
        return None, None

def match_sift_features(desc1, desc2):
    """Match SIFT features between two images and return match score"""
    try:
        if desc1 is None or desc2 is None:
            return 0

        # FLANN parameters
        FLANN_INDEX_KDTREE = 1
        index_params = dict(algorithm=FLANN_INDEX_KDTREE, trees=5)
        search_params = dict(checks=50)

        # Create FLANN matcher
        flann = cv2.FlannBasedMatcher(index_params, search_params)

        # Match descriptors
        matches = flann.knnMatch(desc1, desc2, k=2)

        # Apply ratio test
        good_matches = []
        for m, n in matches:
            if m.distance < 0.7 * n.distance:
                good_matches.append(m)

        # Calculate match score (normalized by number of features)
        match_score = len(good_matches) / max(len(desc1), len(desc2)) if max(len(desc1), len(desc2)) > 0 else 0
        return match_score
    except Exception as e:
        logger.error(f"Error matching SIFT features: {str(e)}")
        return 0

def match_images(proof_image_pil, item_dir):
    """Match a proof image against all images in the item directory using multiple techniques"""
    logger.info(f"Starting image matching in directory: {item_dir}")

    # Compute features for proof image
    proof_phash = compute_phash(proof_image_pil)
    proof_keypoints, proof_descriptors = compute_sift_features(proof_image_pil)
    proof_h_hist, proof_s_hist, proof_v_hist = compute_color_histogram(proof_image_pil)

    results = []

    # Check if directory exists
    if not os.path.exists(item_dir):
        logger.error(f"Directory does not exist: {item_dir}")
        return results

    # List all image files in the directory
    image_files = [f for f in os.listdir(item_dir)
                  if f.lower().endswith((".jpg", ".jpeg", ".png"))]

    logger.info(f"Found {len(image_files)} images to compare")

    for filename in image_files:
        item_path = os.path.join(item_dir, filename)
        try:
            # Open the image
            item_image = Image.open(item_path)

            # Compute perceptual hash distance
            item_phash = compute_phash(item_image)
            if proof_phash is None or item_phash is None:
                phash_distance = 100  # Large value indicating no match
            else:
                phash_distance = proof_phash - item_phash

            # Compute SIFT feature match score
            item_keypoints, item_descriptors = compute_sift_features(item_image)
            sift_score = match_sift_features(proof_descriptors, item_descriptors)

            # Compute color histogram similarity
            item_h_hist, item_s_hist, item_v_hist = compute_color_histogram(item_image)
            h_score = compare_histograms(proof_h_hist, item_h_hist)
            s_score = compare_histograms(proof_s_hist, item_s_hist)
            v_score = compare_histograms(proof_v_hist, item_v_hist)
            color_score = (h_score + s_score + v_score) / 3.0

            # Combine scores (lower is better for phash, higher is better for SIFT and color)
            # Normalize phash distance (0-64) to 0-1 range and invert
            normalized_phash_score = 1 - (phash_distance / 64.0)

            # Combined score (weighted average)
            combined_score = (0.5 * normalized_phash_score) + (0.3 * sift_score) + (0.2 * color_score)

            # Convert to a distance metric (lower is better)
            combined_distance = int((1 - combined_score) * 10)

            # Extract item ID from filename using various possible formats
            item_id = None
            try:
                # Try to extract ID from filename using different patterns
                if "_" in filename:
                    # Format like "item_123.jpg"
                    parts = filename.split("_")
                    if len(parts) > 1:
                        id_part = parts[1].split(".")[0]
                        if id_part.isdigit():
                            item_id = int(id_part)
                elif "-" in filename:
                    # Format like "item-123.jpg"
                    parts = filename.split("-")
                    if len(parts) > 1:
                        id_part = parts[1].split(".")[0]
                        if id_part.isdigit():
                            item_id = int(id_part)
                else:
                    # Try to find any number in the filename
                    import re
                    numbers = re.findall(r'\d+', filename)
                    if numbers:
                        item_id = int(numbers[0])
            except (ValueError, IndexError) as e:
                logger.warning(f"Could not extract item ID from filename {filename}: {e}")

            results.append((filename, combined_distance, phash_distance, sift_score, color_score, item_id))
            logger.info(f"Matched {filename}: Combined={combined_distance}, pHash={phash_distance}, SIFT={sift_score:.3f}, Color={color_score:.3f}")

        except Exception as e:
            logger.error(f"Failed to process {filename}: {str(e)}")

    # Sort by combined distance (lower is better)
    results.sort(key=lambda x: x[1])
    return results[:5]  # Return top 5 matches

def find_item_directories():
    """Find all possible item directories"""
    base_dirs = [
        os.path.join("C:/Users/Tifa/Desktop/Master pull Spring/espritconnect", "uploads", "items"),
        os.path.join("C:/Users/Tifa/Desktop/Master pull Spring/espritconnect/uploads/items"),
        os.path.join("uploads", "items"),
        os.path.join("../uploads/items"),
        os.path.join("../../uploads/items"),
    ]

    valid_dirs = []
    for dir_path in base_dirs:
        if os.path.exists(dir_path) and os.path.isdir(dir_path):
            valid_dirs.append(dir_path)
            logger.info(f"Found valid item directory: {dir_path}")

    return valid_dirs

# === MAIN ===
if __name__ == "__main__":
    start_time = time.time()
    logger.info("Starting improved hybrid matcher")

    if len(sys.argv) < 2:
        logger.error("Please provide the Cloudinary image URL.")
        print("ERROR: Please provide the Cloudinary image URL.")
        sys.exit(1)

    proof_url = sys.argv[1]
    logger.info(f"Processing proof URL: {proof_url}")

    # Step 1: Download image from Cloudinary
    proof_image_pil, proof_image_bytes = download_image_from_url(proof_url)

    if proof_image_pil is None or proof_image_bytes is None:
        logger.error("Failed to download or process the image")
        print("ERROR: Failed to download or process the image")
        sys.exit(1)

    # Step 2: Find valid item directories
    item_dirs = find_item_directories()

    if not item_dirs:
        logger.error("No valid item directories found")
        print("ERROR: No valid item directories found")
        sys.exit(1)

    # Step 3: Match image with all found item directories
    all_matches = []
    for item_dir in item_dirs:
        matches = match_images(proof_image_pil, item_dir)
        all_matches.extend(matches)

    # Sort all matches by combined distance
    all_matches.sort(key=lambda x: x[1])
    best_matches = all_matches[:5]  # Take top 5 overall

    print("MATCHES:")
    for img, combined_dist, phash_dist, sift_score, color_score, item_id in best_matches:
        # Format: filename|combined_distance|phash_distance|sift_score|color_score|item_id
        item_id_str = str(item_id) if item_id is not None else "null"
        print(f"{img}|{combined_dist}|{phash_dist}|{sift_score:.3f}|{color_score:.3f}|{item_id_str}")

    elapsed_time = time.time() - start_time
    logger.info(f"Matching completed in {elapsed_time:.2f} seconds")
