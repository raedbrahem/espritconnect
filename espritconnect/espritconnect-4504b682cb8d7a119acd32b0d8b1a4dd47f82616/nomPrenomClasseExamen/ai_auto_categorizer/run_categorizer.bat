@echo off
REM This batch file runs the enhanced_categorizer.py script with proper path handling

REM Get the directory of this batch file
set SCRIPT_DIR=%~dp0

REM Path to the enhanced_categorizer.py script
set CATEGORIZER_SCRIPT=%SCRIPT_DIR%enhanced_categorizer.py

REM Check if an image path was provided
if "%~1"=="" (
    echo Usage: run_categorizer.bat ^<image_path^>
    exit /b 1
)

REM Get the image path
set IMAGE_PATH=%~1

REM Verify the script exists
if not exist "%CATEGORIZER_SCRIPT%" (
    echo Error: Could not find enhanced_categorizer.py at %CATEGORIZER_SCRIPT%
    exit /b 1
)

REM Verify the image exists
if not exist "%IMAGE_PATH%" (
    echo Error: Could not find image at %IMAGE_PATH%
    exit /b 1
)

REM Run the script
cd "%SCRIPT_DIR%"
python "%CATEGORIZER_SCRIPT%" "%IMAGE_PATH%"

REM Return the exit code
exit /b %ERRORLEVEL%
