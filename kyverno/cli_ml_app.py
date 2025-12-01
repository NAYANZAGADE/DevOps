#!/usr/bin/env python3
"""
CLI version of the ML Application using AWS Bedrock
"""

import argparse
from ml_app import BedrockMLApp

def main():
    parser = argparse.ArgumentParser(description='ML Application CLI')
    parser.add_argument('--task', choices=['analyze', 'generate', 'insights', 'code'], 
                       required=True, help='Task to perform')
    parser.add_argument('--text', help='Text input for analysis or generation')
    parser.add_argument('--type', help='Type of analysis or generation')
    parser.add_argument('--file', help='File to read input from')
    
    args = parser.parse_args()
    
    # Initialize ML app
    ml_app = BedrockMLApp()
    
    # Get input text
    if args.file:
        with open(args.file, 'r') as f:
            input_text = f.read()
    elif args.text:
        input_text = args.text
    else:
        print("Please provide either --text or --file")
        return
    
    # Execute task
    if args.task == 'analyze':
        analysis_type = args.type or 'sentiment'
        result = ml_app.analyze_text(input_text, analysis_type)
    elif args.task == 'generate':
        content_type = args.type or 'general'
        result = ml_app.generate_content(input_text, content_type)
    elif args.task == 'insights':
        result = ml_app.data_insights(input_text)
    elif args.task == 'code':
        language = args.type or 'python'
        result = ml_app.code_analysis(input_text, language)
    
    if result:
        print("Result:")
        print("=" * 50)
        print(result)
    else:
        print("Failed to get result from Bedrock")

if __name__ == "__main__":
    main()