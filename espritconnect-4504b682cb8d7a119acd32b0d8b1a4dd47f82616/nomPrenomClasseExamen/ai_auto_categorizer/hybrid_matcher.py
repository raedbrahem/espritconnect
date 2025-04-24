import sys
import cv2
import numpy as np
import os
from PIL import Image
import imagehash
import requests
from io import BytesIO

import requests
from io import BytesIO
from PIL import Image

def download_image_from_url(url):
    try:
        response = requests.get(url)
        img_bytes = BytesIO(response.content)
        img = Image.open(img_bytes)
        return img, img_bytes
    except Exception as e:
        print("Error downloading image:", str(e))
        return None, None



def match_images(proof_image_pil, item_dir):
    proof_hash = imagehash.phash(proof_image_pil)
    results = []

    for filename in os.listdir(item_dir):
        if filename.lower().endswith((".jpg", ".jpeg", ".png")):
            item_path = os.path.join(item_dir, filename)
            try:
                item_hash = imagehash.phash(Image.open(item_path))
                distance = proof_hash - item_hash
                results.append((filename, distance))
            except Exception as e:
                print(f"⚠️ Failed to process {filename}: {e}")

    results.sort(key=lambda x: x[1])
    return results[:3]

# === MAIN ===
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("❌ Please provide the Cloudinary image URL.")
        sys.exit(1)

    proof_url = sys.argv[1]

    # Step 1: Download image from Cloudinary
    proof_image_pil, proof_image_bytes = download_image_from_url(proof_url)



    # Step 3: Match image hashes with local "items" folder
    item_dir = os.path.join("C:/Users/Tifa/Desktop/PiSpring", "uploads", "items")
    matches = match_images(proof_image_pil, item_dir)

    print("MATCHES:")
    for img, dist in matches:
        print(f"{img}|{dist}")
