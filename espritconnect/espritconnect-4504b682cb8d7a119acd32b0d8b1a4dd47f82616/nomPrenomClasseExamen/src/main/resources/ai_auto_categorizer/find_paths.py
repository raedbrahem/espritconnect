import os
import sys

def find_paths():
    """Find and print the paths to important directories and files"""
    print("Finding paths for AI Auto Categorizer setup...")
    
    # Get the current directory
    current_dir = os.path.abspath(os.getcwd())
    print(f"Current directory: {current_dir}")
    
    # Check if we're in the ai_auto_categorizer directory
    if os.path.basename(current_dir) == "ai_auto_categorizer":
        script_dir = current_dir
    else:
        # Try to find the ai_auto_categorizer directory
        script_dir = None
        for root, dirs, files in os.walk(current_dir, topdown=True):
            if "ai_auto_categorizer" in dirs:
                script_dir = os.path.join(root, "ai_auto_categorizer")
                break
        
        if script_dir is None:
            print("Could not find ai_auto_categorizer directory!")
            return
    
    print(f"AI Auto Categorizer directory: {script_dir}")
    
    # Check for required files
    required_files = ["enhanced_categorizer.py", "yolov3.cfg", "yolov3.weights", "coco.names"]
    missing_files = []
    
    for file in required_files:
        file_path = os.path.join(script_dir, file)
        if os.path.exists(file_path):
            print(f"Found {file}: {file_path}")
        else:
            print(f"Missing {file}!")
            missing_files.append(file)
    
    # Print instructions for missing files
    if missing_files:
        print("\nMissing files detected. Please download the following files:")
        if "yolov3.cfg" in missing_files:
            print("- yolov3.cfg: https://raw.githubusercontent.com/pjreddie/darknet/master/cfg/yolov3.cfg")
        if "yolov3.weights" in missing_files:
            print("- yolov3.weights: https://pjreddie.com/media/files/yolov3.weights")
        if "coco.names" in missing_files:
            print("- coco.names: Already included in the repository")
        
        print("\nYou can also run the download_yolo_files.py script to download these files automatically.")
    
    # Print the paths to use in the Java code
    print("\nPaths to use in the Java code:")
    print(f"Python script path: {os.path.join(script_dir, 'enhanced_categorizer.py')}")
    print(f"Temp directory path: {os.path.join(os.path.dirname(script_dir), 'temp')}")
    
    # Create the temp directory if it doesn't exist
    temp_dir = os.path.join(os.path.dirname(script_dir), "temp")
    if not os.path.exists(temp_dir):
        os.makedirs(temp_dir)
        print(f"Created temp directory: {temp_dir}")

if __name__ == "__main__":
    find_paths()
