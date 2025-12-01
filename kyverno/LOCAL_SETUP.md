# Local Development Setup

Quick guide to run your ML application locally on your machine.

## 🚀 Quick Start (3 steps)

### 1. Install Dependencies
```bash
python setup.py
```

### 2. Configure AWS (if not done already)
```bash
aws configure
# Enter your AWS credentials when prompted
```

### 3. Run the Application
```bash
# Web Interface (Streamlit)
streamlit run ml_app.py

# OR Command Line Interface
python cli_ml_app.py --task analyze --text "I love this product!"
```

## 📋 Prerequisites

- **Python 3.8+** installed
- **AWS CLI** installed and configured
- **AWS Bedrock model access** (Claude 3 Sonnet, Titan)

## 🔧 Manual Setup (if needed)

### Install Python Dependencies
```bash
pip install -r requirements.txt
```

### Set AWS Credentials (choose one method)

**Method 1: AWS CLI**
```bash
aws configure
```

**Method 2: Environment Variables**
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_DEFAULT_REGION=us-east-1
```

**Method 3: AWS Profile**
```bash
export AWS_PROFILE=your_profile_name
```

## 🎯 Running the Application

### Streamlit Web Interface
```bash
streamlit run ml_app.py
```
- Opens in browser at `http://localhost:8501`
- Interactive UI with multiple ML tasks
- Real-time results

### Command Line Interface
```bash
# Text Analysis
python cli_ml_app.py --task analyze --text "This is amazing!" --type sentiment

# Content Generation
python cli_ml_app.py --task generate --text "Write a story about AI" --type creative

# Data Insights
python cli_ml_app.py --task insights --text "Customer feedback dataset with 1000 reviews"

# Code Analysis
python cli_ml_app.py --task code --file your_script.py --type python
```

## 🧪 Testing Your Setup

### Quick Test
```bash
python -c "
import boto3
client = boto3.client('bedrock-runtime', region_name='us-east-1')
print('✅ Bedrock client created successfully!')
"
```

### Test with Sample Data
```bash
python cli_ml_app.py --task analyze --text "I absolutely love this new feature!" --type sentiment
```

## 🔍 Troubleshooting

### Common Issues

**1. AWS Credentials Not Found**
```bash
# Check current credentials
aws sts get-caller-identity

# If error, configure credentials
aws configure
```

**2. Bedrock Access Denied / Use Case Details Required**
```bash
# Check your model access status
python check-bedrock-access.py
```
- Go to [AWS Bedrock Console](https://console.aws.amazon.com/bedrock/)
- Click "Model access" → "Request model access"
- Select Claude 3 Sonnet and fill out the use case form
- Wait for approval (usually instant, max 15 minutes)
- **Meanwhile**: Your app will automatically use Titan as fallback!

**3. Module Not Found Errors**
```bash
# Reinstall dependencies
pip install -r requirements.txt --upgrade
```

**4. Port Already in Use (Streamlit)**
```bash
# Use different port
streamlit run ml_app.py --server.port 8502
```

## 🎨 Development Tips

### Hot Reload Development
```bash
# Streamlit auto-reloads on file changes
streamlit run ml_app.py --server.runOnSave true
```

### Debug Mode
```bash
# Add debug prints to your code
export STREAMLIT_LOGGER_LEVEL=debug
streamlit run ml_app.py
```

### Custom Configuration
Create `.streamlit/config.toml`:
```toml
[server]
port = 8501
address = "0.0.0.0"

[theme]
primaryColor = "#FF6B6B"
backgroundColor = "#FFFFFF"
secondaryBackgroundColor = "#F0F2F6"
```

## 📊 Performance Tips

### Faster Model Responses
- Use smaller `max_tokens` for quicker responses
- Cache responses for repeated queries
- Use appropriate model for task (Titan for creative, Claude for analysis)

### Memory Optimization
```bash
# Monitor memory usage
pip install psutil
python -c "import psutil; print(f'Memory: {psutil.virtual_memory().percent}%')"
```

## 🔒 Security Best Practices

### Environment Variables
Create `.env` file:
```bash
AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
AWS_DEFAULT_REGION=us-east-1
```

Load in Python:
```python
from dotenv import load_dotenv
load_dotenv()
```

### Credential Rotation
```bash
# Regularly rotate AWS keys
aws iam create-access-key --user-name your-username
```

## 📱 Mobile/Remote Access

### Access from Other Devices
```bash
# Run on all interfaces
streamlit run ml_app.py --server.address 0.0.0.0
# Access via http://your-ip:8501
```

### Tunnel for Remote Access
```bash
# Using ngrok (install first)
ngrok http 8501
```

## 🚀 Next Steps

1. **Customize the UI** - Modify `ml_app.py` for your needs
2. **Add new models** - Extend `BedrockMLApp` class
3. **Create workflows** - Chain multiple AI tasks
4. **Add data sources** - Connect to databases, APIs
5. **Deploy to cloud** - Use the EKS deployment when ready

## 📞 Getting Help

If you encounter issues:
1. Check the troubleshooting section above
2. Verify AWS Bedrock model access in console
3. Test AWS credentials with `aws sts get-caller-identity`
4. Check Python version with `python --version`