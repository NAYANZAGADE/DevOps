@echo off
echo 🤖 ML Application Local Runner
echo ================================

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Python is not installed
    pause
    exit /b 1
)

REM Create virtual environment if it doesn't exist
if not exist "venv" (
    echo 📦 Creating virtual environment...
    python -m venv venv
)

REM Activate virtual environment
echo 🔧 Activating virtual environment...
call venv\Scripts\activate.bat

REM Install dependencies
echo 📥 Installing dependencies...
pip install -r requirements.txt --quiet

REM Check AWS credentials
echo 🔐 Checking AWS credentials...
aws sts get-caller-identity >nul 2>&1
if errorlevel 1 (
    echo ❌ AWS credentials not configured
    echo 💡 Please run: aws configure
    pause
    exit /b 1
) else (
    echo ✅ AWS credentials found
)

REM Show menu
echo.
echo Choose how to run the application:
echo 1) Streamlit Web Interface (recommended)
echo 2) Command Line Interface  
echo 3) Quick test
echo 4) Exit

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo 🚀 Starting Streamlit web interface...
    echo 📍 Open http://localhost:8501 in your browser
    streamlit run ml_app.py
) else if "%choice%"=="2" (
    echo 💻 Command Line Interface
    echo Example: python cli_ml_app.py --task analyze --text "I love this!" --type sentiment
    echo.
    set /p cmd="Enter your command (or press Enter for example): "
    if "%cmd%"=="" (
        python cli_ml_app.py --task analyze --text "This is an amazing ML application!" --type sentiment
    ) else (
        %cmd%
    )
) else if "%choice%"=="3" (
    echo 🧪 Running quick test...
    python -c "import boto3; client = boto3.client('bedrock-runtime', region_name='us-east-1'); print('✅ Bedrock client created successfully!'); print('🎯 Ready to run ML tasks!')"
) else if "%choice%"=="4" (
    echo 👋 Goodbye!
    exit /b 0
) else (
    echo ❌ Invalid choice
    pause
    exit /b 1
)

pause