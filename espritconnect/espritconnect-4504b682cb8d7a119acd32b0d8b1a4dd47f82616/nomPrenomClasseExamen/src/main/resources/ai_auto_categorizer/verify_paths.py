import os
import sys

def verify_paths():
    """Verify that the paths used in the Java code are correct"""
    print("Verifying paths for AI Auto Categorizer...")

    # Check the Python script path - try multiple possible locations
    possible_script_paths = [
        os.path.join(os.getcwd(), "ai_auto_categorizer", "enhanced_categorizer.py"),
        os.path.join(os.getcwd(), "enhanced_categorizer.py"),
        os.path.join(os.path.dirname(os.getcwd()), "ai_auto_categorizer", "enhanced_categorizer.py"),
        os.path.join(os.path.dirname(os.path.dirname(os.getcwd())), "ai_auto_categorizer", "enhanced_categorizer.py")
    ]

    script_path = None
    for path in possible_script_paths:
        path = path.replace("/", os.path.sep)
        if os.path.exists(path):
            script_path = path
            print(f"✅ Python script found at: {script_path}")
            break

    if script_path is None:
        print("❌ Python script not found in any of the expected locations")
        # Use a default path for further checks
        script_path = os.path.join(os.getcwd(), "ai_auto_categorizer", "enhanced_categorizer.py")

    # Check the temp directory path - use a relative path from the current directory
    temp_dir_path = os.path.join(os.path.dirname(os.getcwd()), "temp")
    if not os.path.exists(os.path.dirname(temp_dir_path)):
        # Try a different location
        temp_dir_path = os.path.join(os.getcwd(), "temp")

    if os.path.exists(temp_dir_path):
        print(f"✅ Temp directory found at: {temp_dir_path}")
    else:
        print(f"❌ Temp directory NOT found at: {temp_dir_path}")
        # Try to create the temp directory
        try:
            os.makedirs(temp_dir_path)
            print(f"✅ Created temp directory at: {temp_dir_path}")
        except Exception as e:
            print(f"❌ Failed to create temp directory: {str(e)}")

    # Check for required YOLO files
    yolo_files = ["yolov3.weights", "yolov3.cfg", "coco.names"]
    for file in yolo_files:
        file_path = os.path.join(os.path.dirname(script_path), file)
        if os.path.exists(file_path):
            print(f"✅ {file} found at: {file_path}")
        else:
            print(f"❌ {file} NOT found at: {file_path}")

    # Try to run the enhanced_categorizer.py script with a test image
    print("\nTrying to run the enhanced_categorizer.py script...")
    try:
        # Create a test image if needed
        test_image_path = os.path.join(temp_dir_path, "test_image.jpg")
        if not os.path.exists(test_image_path):
            # Create a simple test image
            with open(test_image_path, "wb") as f:
                f.write(b"\xff\xd8\xff\xe0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xff\xdb\x00C\x00\x08\x06\x06\x07\x06\x05\x08\x07\x07\x07\t\t\x08\n\x0c\x14\r\x0c\x0b\x0b\x0c\x19\x12\x13\x0f\x14\x1d\x1a\x1f\x1e\x1d\x1a\x1c\x1c $.' \",#\x1c\x1c(7),01444\x1f'9=82<.342\xff\xdb\x00C\x01\t\t\t\x0c\x0b\x0c\x18\r\r\x182!\x1c!22222222222222222222222222222222222222222222222222\xff\xc0\x00\x11\x08\x00\x01\x00\x01\x03\x01\"\x00\x02\x11\x01\x03\x11\x01\xff\xc4\x00\x1f\x00\x00\x01\x05\x01\x01\x01\x01\x01\x01\x00\x00\x00\x00\x00\x00\x00\x00\x01\x02\x03\x04\x05\x06\x07\x08\t\n\x0b\xff\xc4\x00\xb5\x10\x00\x02\x01\x03\x03\x02\x04\x03\x05\x05\x04\x04\x00\x00\x01}\x01\x02\x03\x00\x04\x11\x05\x12!1A\x06\x13Qa\x07\"q\x142\x81\x91\xa1\x08#B\xb1\xc1\x15R\xd1\xf0$3br\x82\t\n\x16\x17\x18\x19\x1a%&'()*456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz\x83\x84\x85\x86\x87\x88\x89\x8a\x92\x93\x94\x95\x96\x97\x98\x99\x9a\xa2\xa3\xa4\xa5\xa6\xa7\xa8\xa9\xaa\xb2\xb3\xb4\xb5\xb6\xb7\xb8\xb9\xba\xc2\xc3\xc4\xc5\xc6\xc7\xc8\xc9\xca\xd2\xd3\xd4\xd5\xd6\xd7\xd8\xd9\xda\xe1\xe2\xe3\xe4\xe5\xe6\xe7\xe8\xe9\xea\xf1\xf2\xf3\xf4\xf5\xf6\xf7\xf8\xf9\xfa\xff\xc4\x00\x1f\x01\x00\x03\x01\x01\x01\x01\x01\x01\x01\x01\x01\x00\x00\x00\x00\x00\x00\x01\x02\x03\x04\x05\x06\x07\x08\t\n\x0b\xff\xc4\x00\xb5\x11\x00\x02\x01\x02\x04\x04\x03\x04\x07\x05\x04\x04\x00\x01\x02w\x00\x01\x02\x03\x11\x04\x05!1\x06\x12AQ\x07aq\x13\"2\x81\x08\x14B\x91\xa1\xb1\xc1\t#3R\xf0\x15br\xd1\n\x16$4\xe1%\xf1\x17\x18\x19\x1a&'()*56789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz\x82\x83\x84\x85\x86\x87\x88\x89\x8a\x92\x93\x94\x95\x96\x97\x98\x99\x9a\xa2\xa3\xa4\xa5\xa6\xa7\xa8\xa9\xaa\xb2\xb3\xb4\xb5\xb6\xb7\xb8\xb9\xba\xc2\xc3\xc4\xc5\xc6\xc7\xc8\xc9\xca\xd2\xd3\xd4\xd5\xd6\xd7\xd8\xd9\xda\xe2\xe3\xe4\xe5\xe6\xe7\xe8\xe9\xea\xf2\xf3\xf4\xf5\xf6\xf7\xf8\xf9\xfa\xff\xda\x00\x0c\x03\x01\x00\x02\x11\x03\x11\x00?\x00\xfe\xfe(\xa2\x8a\x00\xff\xd9")
            print(f"✅ Created test image at: {test_image_path}")

        # Run the script
        script_dir = os.path.dirname(script_path)
        os.chdir(script_dir)

        # Use subprocess instead of os.system to handle spaces in paths
        import subprocess

        # Quote the paths to handle spaces
        quoted_script_path = f'"{script_path}"'
        quoted_test_image_path = f'"{test_image_path}"'

        cmd = ["python", script_path, test_image_path]
        print(f"Running command: {' '.join(cmd)}")

        try:
            # Use subprocess.run to handle spaces in paths
            result = subprocess.run(cmd, check=False, capture_output=True, text=True)

            # Print the output
            if result.stdout:
                print("Output:")
                print(result.stdout)

            # Print any errors
            if result.stderr:
                print("Errors:")
                print(result.stderr)

            if result.returncode == 0:
                print("✅ Script ran successfully!")
            else:
                print(f"❌ Script failed with exit code: {result.returncode}")

            # Return the result code for compatibility with the rest of the code
            result = result.returncode
        except Exception as e:
            print(f"❌ Error running script: {str(e)}")
            result = 1

        if result == 0:
            print("✅ Script ran successfully!")
        else:
            print(f"❌ Script failed with exit code: {result}")

    except Exception as e:
        print(f"❌ Error running script: {str(e)}")

if __name__ == "__main__":
    verify_paths()
