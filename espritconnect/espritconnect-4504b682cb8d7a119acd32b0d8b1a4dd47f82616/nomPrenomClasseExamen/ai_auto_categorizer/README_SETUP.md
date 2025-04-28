# AI Auto Categorizer Setup Guide

This guide explains how to set up the AI Auto Categorizer on a new machine.

## Prerequisites

1. Python 3.6 or higher
2. OpenCV for Python
3. NumPy
4. PIL (Python Imaging Library)

## Setup Steps

1. **Find Correct Paths**

   First, run the find_paths.py script to locate the correct directories and check for missing files:

   ```bash
   python find_paths.py
   ```

   This script will:
   - Find the AI Auto Categorizer directory
   - Check for required files
   - Create the temp directory if needed
   - Print the paths to use in the Java code

2. **Install Python Dependencies**

   Open a command prompt and run:

   ```bash
   pip install opencv-python numpy pillow requests
   ```

3. **Download YOLO Model Files**

   The AI categorizer requires three files to work properly:

   - `yolov3.cfg` - Configuration file for YOLO
   - `yolov3.weights` - Pre-trained weights for the YOLO model
   - `coco.names` - Class names for the YOLO model

   You can download these files by running the provided script:

   ```bash
   python download_yolo_files.py
   ```

   Or download them manually:

   - `yolov3.cfg`: https://raw.githubusercontent.com/pjreddie/darknet/master/cfg/yolov3.cfg
   - `yolov3.weights`: https://pjreddie.com/media/files/yolov3.weights
   - `coco.names`: Already included in the repository

4. **Verify File Structure**

   Make sure the following files are in the `ai_auto_categorizer` directory:

   - `enhanced_categorizer.py`
   - `yolov3.cfg`
   - `yolov3.weights`
   - `coco.names`

5. **Test the Categorizer**

   You can test the categorizer by running:

   ```bash
   python test_categorizer.py path/to/test/image.jpg
   ```

## Troubleshooting

If you encounter issues with the AI categorizer:

1. **Check the logs** - Look for error messages in the application logs.

2. **Run the find_paths.py script** - This will help identify any path issues:

   ```bash
   python find_paths.py
   ```

3. **Verify Python installation** - Make sure Python is installed and in your PATH.

4. **Check file paths** - The application automatically finds the correct paths, but you can verify them in the logs.

5. **Missing YOLO files** - If you see "Missing YOLO model files" errors, run the download script again:

   ```bash
   python download_yolo_files.py
   ```

6. **Path issues in Java** - If you see path-related errors in the Java logs, you may need to manually set the paths in the CategoryPredictionService class. Look for the following lines in the logs to find the correct paths:

   ```
   Python script path: /path/to/ai_auto_categorizer/enhanced_categorizer.py
   Temp directory path: /path/to/temp
   ```

7. **OpenCV errors** - If you see OpenCV-related errors, try reinstalling it:

   ```bash
   pip uninstall opencv-python
   pip install opencv-python
   ```

8. **Test the Python script directly** - You can test the Python script directly to verify it works:

   ```bash
   python enhanced_categorizer.py path/to/test/image.jpg
   ```

## How It Works

The AI categorizer uses a combination of techniques to identify the category of an item:

1. **YOLO Object Detection** - Identifies common objects in the image
2. **Specialized Detectors** - Additional detectors for specific categories like keys, documents, and clothing
3. **Color and Texture Analysis** - Analyzes image properties to help with categorization

The categorizer returns the most likely category along with a confidence score.
