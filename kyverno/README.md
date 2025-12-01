# ML Application with AWS Bedrock

A comprehensive machine learning application that leverages AWS Bedrock's foundation models for various AI tasks.

## Features

- **Text Analysis**: Sentiment analysis, classification, summarization, entity extraction
- **Content Generation**: Creative and technical content generation
- **Data Insights**: AI-powered data analysis and recommendations
- **Code Analysis**: Code quality assessment and improvement suggestions
- **Multiple Interfaces**: Both Streamlit web UI and CLI

## Setup

1. **Install dependencies**:
   ```bash
   python setup.py
   ```

2. **Configure AWS credentials** (if not already done):
   ```bash
   aws configure
   ```

3. **Ensure Bedrock model access** in your AWS account:
   - Go to AWS Bedrock console
   - Request access to Claude 3 Sonnet and Titan models

## Usage

### Streamlit Web Interface
```bash
streamlit run ml_app.py
```

### Command Line Interface
```bash
# Text analysis
python cli_ml_app.py --task analyze --text "I love this product!" --type sentiment

# Content generation
python cli_ml_app.py --task generate --text "Write a story about AI" --type creative

# Data insights
python cli_ml_app.py --task insights --text "Dataset with 1000 customer reviews"

# Code analysis
python cli_ml_app.py --task code --file your_code.py --type python
```

## Available Models

- **Claude 3 Sonnet**: Best for complex reasoning, analysis, and technical tasks
- **Amazon Titan**: Great for creative content generation
- **AI21 Jurassic**: Alternative text generation model

## File Structure

```
├── ml_app.py          # Main Streamlit application
├── cli_ml_app.py      # Command line interface
├── import boto3.py    # Basic Bedrock client setup
├── setup.py           # Setup and installation script
├── requirements.txt   # Python dependencies
└── README.md         # This file
```

## AWS Configuration

Make sure your AWS credentials have permissions for:
- `bedrock:InvokeModel`
- Access to the specific models you want to use

## Troubleshooting

1. **Model access denied**: Request access to models in Bedrock console
2. **Credentials error**: Run `aws configure` or set environment variables
3. **Region issues**: Update region in `BedrockMLApp.__init__()` if needed

## Extending the Application

You can easily add new features by:
1. Adding new methods to the `BedrockMLApp` class
2. Creating new UI components in the Streamlit app
3. Adding new CLI commands

Happy coding! 🚀