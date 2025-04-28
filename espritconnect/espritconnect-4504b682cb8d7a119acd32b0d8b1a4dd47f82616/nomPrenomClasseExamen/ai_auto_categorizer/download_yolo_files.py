import urllib.request
import os
import sys

def download_file(url, filename):
    """Download a file from a URL to the specified filename"""
    print(f"Downloading {filename} from {url}...")
    try:
        urllib.request.urlretrieve(url, filename)
        print(f"Successfully downloaded {filename}")
        return True
    except Exception as e:
        print(f"Error downloading {filename}: {str(e)}")
        return False

def main():
    # URLs for YOLO files
    yolov3_cfg_url = "https://raw.githubusercontent.com/pjreddie/darknet/master/cfg/yolov3.cfg"
    yolov3_weights_url = "https://pjreddie.com/media/files/yolov3.weights"

    # Download yolov3.cfg
    if not os.path.exists("yolov3.cfg"):
        if download_file(yolov3_cfg_url, "yolov3.cfg"):
            print("yolov3.cfg downloaded successfully")
        else:
            print("Failed to download yolov3.cfg")
    else:
        print("yolov3.cfg already exists")

    # Download yolov3.weights (this is a large file, ~236MB)
    if not os.path.exists("yolov3.weights"):
        print("Downloading yolov3.weights (this is a large file, ~236MB)...")
        print("This may take a while...")
        if download_file(yolov3_weights_url, "yolov3.weights"):
            print("yolov3.weights downloaded successfully")
        else:
            print("Failed to download yolov3.weights")
    else:
        print("yolov3.weights already exists")

    # Verify files exist
    if os.path.exists("yolov3.cfg") and os.path.exists("yolov3.weights") and os.path.exists("coco.names"):
        print("All required YOLO files are present. The categorizer should work now.")
    else:
        print("Some required files are missing:")
        if not os.path.exists("yolov3.cfg"):
            print("- yolov3.cfg is missing")
        if not os.path.exists("yolov3.weights"):
            print("- yolov3.weights is missing")
        if not os.path.exists("coco.names"):
            print("- coco.names is missing")

if __name__ == "__main__":
    main()
