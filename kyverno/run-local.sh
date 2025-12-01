#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🤖 ML Application Local Runner${NC}"
echo -e "${BLUE}================================${NC}"

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Python
if ! command_exists python3; then
    echo -e "${RED}❌ Python 3 is not installed${NC}"
    exit 1
fi

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    echo -e "${YELLOW}📦 Creating virtual environment...${NC}"
    python3 -m venv venv
fi

# Activate virtual environment
echo -e "${YELLOW}🔧 Activating virtual environment...${NC}"
source venv/bin/activate

# Install/upgrade dependencies
echo -e "${YELLOW}📥 Installing dependencies...${NC}"
pip install -r requirements.txt --quiet

# Check AWS credentials
echo -e "${YELLOW}🔐 Checking AWS credentials...${NC}"
if ! aws sts get-caller-identity >/dev/null 2>&1; then
    echo -e "${RED}❌ AWS credentials not configured${NC}"
    echo -e "${YELLOW}💡 Please run: aws configure${NC}"
    exit 1
else
    echo -e "${GREEN}✅ AWS credentials found${NC}"
fi

# Show menu
echo -e "\n${BLUE}Choose how to run the application:${NC}"
echo -e "${GREEN}1)${NC} Streamlit Web Interface (recommended)"
echo -e "${GREEN}2)${NC} Command Line Interface"
echo -e "${GREEN}3)${NC} Quick test"
echo -e "${GREEN}4)${NC} Exit"

read -p "Enter your choice (1-4): " choice

case $choice in
    1)
        echo -e "${GREEN}🚀 Starting Streamlit web interface...${NC}"
        echo -e "${BLUE}📍 Open http://localhost:8501 in your browser${NC}"
        streamlit run ml_app.py
        ;;
    2)
        echo -e "${GREEN}💻 Command Line Interface${NC}"
        echo -e "${YELLOW}Example commands:${NC}"
        echo -e "  python cli_ml_app.py --task analyze --text 'I love this!' --type sentiment"
        echo -e "  python cli_ml_app.py --task generate --text 'Write a poem' --type creative"
        echo ""
        read -p "Enter your command (or press Enter for example): " cmd
        if [ -z "$cmd" ]; then
            python cli_ml_app.py --task analyze --text "This is an amazing ML application!" --type sentiment
        else
            eval $cmd
        fi
        ;;
    3)
        echo -e "${GREEN}🧪 Running quick test...${NC}"
        python -c "
import boto3
try:
    client = boto3.client('bedrock-runtime', region_name='us-east-1')
    print('✅ Bedrock client created successfully!')
    print('🎯 Ready to run ML tasks!')
except Exception as e:
    print(f'❌ Error: {e}')
"
        ;;
    4)
        echo -e "${GREEN}👋 Goodbye!${NC}"
        exit 0
        ;;
    *)
        echo -e "${RED}❌ Invalid choice${NC}"
        exit 1
        ;;
esac