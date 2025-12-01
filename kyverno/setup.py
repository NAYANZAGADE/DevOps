#!/usr/bin/env python3
"""
Setup script for the ML Application
"""

import subprocess
import sys
import os

def install_requirements():
    """Install required packages"""
    print("Installing requirements...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "-r", "requirements.txt"])

def check_aws_credentials():
    """Check if AWS credentials are configured"""
    try:
        import boto3
        session = boto3.Session()
        credentials = session.get_credentials()
        if credentials is None:
            print("❌ AWS credentials not found!")
            print("Please configure AWS credentials using:")
            print("  aws configure")
            print("  or set environment variables:")
            print("  export AWS_ACCESS_KEY_ID=your_key")
            print("  export AWS_SECRET_ACCESS_KEY=your_secret")
            return False
        else:
            print("✅ AWS credentials found")
            return True
    except Exception as e:
        print(f"❌ Error checking AWS credentials: {e}")
        return False

def main():
    print("🤖 Setting up ML Application with Bedrock")
    print("=" * 50)
    
    # Install requirements
    try:
        install_requirements()
        print("✅ Requirements installed successfully")
    except Exception as e:
        print(f"❌ Failed to install requirements: {e}")
        return
    
    # Check AWS credentials
    if not check_aws_credentials():
        return
    
    print("\n🎉 Setup complete!")
    print("\nTo run the application:")
    print("  Streamlit UI: streamlit run ml_app.py")
    print("  CLI: python cli_ml_app.py --task analyze --text 'Your text here'")
    print("\nMake sure you have access to Bedrock models in your AWS account!")

if __name__ == "__main__":
    main()