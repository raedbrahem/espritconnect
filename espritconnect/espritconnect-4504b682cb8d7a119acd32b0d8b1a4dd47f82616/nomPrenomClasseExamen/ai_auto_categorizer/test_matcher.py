import sys
import requests
import argparse
from enhanced_hybrid_matcher import download_image_from_url, match_images, predict_category
from PIL import Image
import os
import time
import logging

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('test_matcher')

def test_url_download(url):
    """Test downloading an image from a URL"""
    print(f"Testing URL download: {url}")
    img, img_bytes = download_image_from_url(url)
    
    if img is None:
        print("❌ Failed to download image")
        return False
    
    print(f"✅ Successfully downloaded image: {img.size} pixels")
    return True

def test_category_prediction(url):
    """Test category prediction on an image"""
    print(f"Testing category prediction for: {url}")
    img, img_bytes = download_image_from_url(url)
    
    if img is None:
        print("❌ Failed to download image")
        return
    
    category = predict_category(img_bytes)
    print(f"Predicted category: {category}")

def test_image_matching(url, items_dir=None):
    """Test matching an image against a directory of images"""
    print(f"Testing image matching for: {url}")
    img, img_bytes = download_image_from_url(url)
    
    if img is None:
        print("❌ Failed to download image")
        return
    
    if items_dir is None:
        items_dir = os.path.join("C:/Users/Tifa/Desktop/PiSpring", "uploads", "items")
    
    if not os.path.exists(items_dir):
        print(f"❌ Items directory does not exist: {items_dir}")
        return
    
    print(f"Matching against items in: {items_dir}")
    matches = match_images(img, items_dir)
    
    if not matches:
        print("No matches found")
    else:
        print("Matches:")
        for img, combined_dist, phash_dist, sift_score in matches:
            print(f"  {img}: Combined={combined_dist}, pHash={phash_dist}, SIFT={sift_score:.3f}")

def main():
    parser = argparse.ArgumentParser(description='Test the enhanced hybrid matcher')
    parser.add_argument('--url', type=str, help='URL of the image to test')
    parser.add_argument('--test', choices=['download', 'category', 'matching', 'all'], 
                        default='all', help='Test to run')
    parser.add_argument('--items-dir', type=str, help='Directory containing items to match against')
    
    args = parser.parse_args()
    
    if args.url is None:
        print("Please provide a URL with --url")
        return
    
    if args.test == 'download' or args.test == 'all':
        test_url_download(args.url)
    
    if args.test == 'category' or args.test == 'all':
        test_category_prediction(args.url)
    
    if args.test == 'matching' or args.test == 'all':
        test_image_matching(args.url, args.items_dir)

if __name__ == "__main__":
    main()
