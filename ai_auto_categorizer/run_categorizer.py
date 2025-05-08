import os
import sys
import subprocess

def run_categorizer(image_path):
    """Run the enhanced_categorizer.py script with proper path handling"""
    # Get the directory of this script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Path to the enhanced_categorizer.py script
    categorizer_script = os.path.join(script_dir, "enhanced_categorizer.py")
    
    # Verify the script exists
    if not os.path.exists(categorizer_script):
        print(f"Error: Could not find enhanced_categorizer.py at {categorizer_script}")
        return 1
    
    # Verify the image exists
    if not os.path.exists(image_path):
        print(f"Error: Could not find image at {image_path}")
        return 1
    
    # Run the script using subprocess to handle spaces in paths
    try:
        # Change to the script directory
        os.chdir(script_dir)
        
        # Run the script
        result = subprocess.run(
            ["python", categorizer_script, image_path],
            check=False,
            capture_output=True,
            text=True
        )
        
        # Print the output
        if result.stdout:
            print(result.stdout)
        
        # Print any errors
        if result.stderr:
            print("Errors:", file=sys.stderr)
            print(result.stderr, file=sys.stderr)
        
        return result.returncode
    
    except Exception as e:
        print(f"Error running script: {str(e)}", file=sys.stderr)
        return 1

if __name__ == "__main__":
    # Check if an image path was provided
    if len(sys.argv) < 2:
        print("Usage: python run_categorizer.py <image_path>")
        sys.exit(1)
    
    # Get the image path
    image_path = sys.argv[1]
    
    # Run the categorizer
    exit_code = run_categorizer(image_path)
    
    # Exit with the same code
    sys.exit(exit_code)
