#!/usr/bin/env python3
"""
ML Application using AWS Bedrock
A comprehensive ML application with multiple AI capabilities
"""

import boto3
import json
import streamlit as st
import pandas as pd
from botocore.exceptions import ClientError
from typing import Optional, Dict, Any
import base64
from io import BytesIO

class BedrockMLApp:
    def __init__(self, region_name='us-east-1'):
        """Initialize the ML application with Bedrock client"""
        self.bedrock_client = boto3.client('bedrock-runtime', region_name=region_name)
        self.models = {
            'claude': "anthropic.claude-3-sonnet-20240229-v1:0",
            'titan': "amazon.titan-text-express-v1",
            'jurassic': "ai21.j2-ultra-v1"
        }
    
    def analyze_text(self, text: str, analysis_type: str = "sentiment") -> Optional[str]:
        """Analyze text using Claude for various ML tasks"""
        prompts = {
            "sentiment": f"Analyze the sentiment of this text and provide a detailed analysis: {text}",
            "classification": f"Classify this text into categories and explain your reasoning: {text}",
            "summarization": f"Provide a concise summary of this text: {text}",
            "entity_extraction": f"Extract all named entities from this text and categorize them: {text}"
        }
        
        prompt = prompts.get(analysis_type, prompts["sentiment"])
        return self._invoke_claude(prompt)
    
    def generate_content(self, prompt: str, content_type: str = "general") -> Optional[str]:
        """Generate content using different models based on type"""
        if content_type == "creative":
            return self._invoke_titan(f"Create creative content: {prompt}")
        elif content_type == "technical":
            return self._invoke_claude(f"Provide technical analysis: {prompt}")
        else:
            return self._invoke_claude(prompt)
    
    def data_insights(self, data_description: str) -> Optional[str]:
        """Generate insights about data using Claude"""
        prompt = f"""
        As a data scientist, analyze this data description and provide:
        1. Key insights and patterns
        2. Potential ML applications
        3. Recommended analysis approaches
        4. Data quality considerations
        
        Data description: {data_description}
        """
        return self._invoke_claude(prompt)
    
    def code_analysis(self, code: str, language: str = "python") -> Optional[str]:
        """Analyze code for bugs, improvements, and best practices"""
        prompt = f"""
        Analyze this {language} code and provide:
        1. Code quality assessment
        2. Potential bugs or issues
        3. Performance improvements
        4. Best practice recommendations
        
        Code:
        {code}
        """
        return self._invoke_claude(prompt)
    
    def _invoke_claude(self, prompt: str, max_tokens: int = 1000) -> Optional[str]:
        """Private method to invoke Claude model with Titan fallback"""
        body = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": max_tokens,
            "messages": [{"role": "user", "content": prompt}]
        }
        
        try:
            response = self.bedrock_client.invoke_model(
                modelId=self.models['claude'],
                body=json.dumps(body),
                contentType='application/json'
            )
            response_body = json.loads(response['body'].read())
            return response_body['content'][0]['text']
        except ClientError as e:
            if "ResourceNotFoundException" in str(e) or "use case details" in str(e):
                st.warning("⚠️ Claude access not available. Using Titan as fallback...")
                # Simplify prompt for Titan to avoid content restrictions
                simplified_prompt = self._simplify_prompt_for_titan(prompt)
                return self._invoke_titan(simplified_prompt, max_tokens)
            else:
                st.error(f"Error invoking Claude: {e}")
                return None
    
    def _simplify_prompt_for_titan(self, prompt: str) -> str:
        """Simplify prompts to work better with Titan's content policies"""
        # Remove complex instructions that might trigger Titan's restrictions
        if "sentiment" in prompt.lower():
            # Extract just the text for sentiment analysis
            text_start = prompt.find(": ") + 2
            text = prompt[text_start:] if text_start > 1 else prompt
            return f"Analyze the sentiment of this text as positive, negative, or neutral: {text}"
        elif "classify" in prompt.lower():
            text_start = prompt.find(": ") + 2
            text = prompt[text_start:] if text_start > 1 else prompt
            return f"Categorize this text: {text}"
        elif "summarize" in prompt.lower() or "summary" in prompt.lower():
            text_start = prompt.find(": ") + 2
            text = prompt[text_start:] if text_start > 1 else prompt
            return f"Summarize this text: {text}"
        elif "extract" in prompt.lower() and "entities" in prompt.lower():
            text_start = prompt.find(": ") + 2
            text = prompt[text_start:] if text_start > 1 else prompt
            return f"Find the important names, places, and organizations in this text: {text}"
        else:
            return prompt
    
    def _invoke_titan(self, prompt: str, max_tokens: int = 512) -> Optional[str]:
        """Private method to invoke Titan model"""
        body = {
            "inputText": prompt,
            "textGenerationConfig": {
                "maxTokenCount": max_tokens,
                "temperature": 0.3,  # Lower temperature for more focused responses
                "topP": 0.9,
                "stopSequences": []
            }
        }
        
        try:
            response = self.bedrock_client.invoke_model(
                modelId=self.models['titan'],
                body=json.dumps(body),
                contentType='application/json'
            )
            response_body = json.loads(response['body'].read())
            result = response_body['results'][0]['outputText'].strip()
            
            # Check if Titan refused to answer
            refusal_phrases = [
                "unable to provide opinions",
                "cannot provide moral judgements",
                "I can't help with that",
                "I'm not able to"
            ]
            
            if any(phrase in result.lower() for phrase in refusal_phrases):
                return "I apologize, but I'm having trouble processing this request with the current model. Please try a simpler prompt or wait for Claude access to be approved."
            
            return result
            
        except ClientError as e:
            st.error(f"Error invoking Titan: {e}")
            return None

def main():
    """Main Streamlit application"""
    st.set_page_config(
        page_title="ML Application with Bedrock",
        page_icon="🤖",
        layout="wide"
    )
    
    st.title("🤖 ML Application with AWS Bedrock")
    st.markdown("A comprehensive ML application powered by AWS Bedrock models")
    
    # Initialize the ML app
    if 'ml_app' not in st.session_state:
        st.session_state.ml_app = BedrockMLApp()
    
    # Sidebar for navigation
    st.sidebar.title("ML Tasks")
    task = st.sidebar.selectbox(
        "Choose a task:",
        ["Text Analysis", "Content Generation", "Data Insights", "Code Analysis"]
    )
    
    if task == "Text Analysis":
        st.header("📝 Text Analysis")
        
        text_input = st.text_area("Enter text to analyze:", height=150)
        analysis_type = st.selectbox(
            "Analysis type:",
            ["sentiment", "classification", "summarization", "entity_extraction"]
        )
        
        if st.button("Analyze Text"):
            if text_input:
                with st.spinner("Analyzing..."):
                    result = st.session_state.ml_app.analyze_text(text_input, analysis_type)
                    if result:
                        st.success("Analysis complete!")
                        st.write(result)
            else:
                st.warning("Please enter some text to analyze.")
    
    elif task == "Content Generation":
        st.header("✨ Content Generation")
        
        prompt = st.text_area("Enter your prompt:", height=100)
        content_type = st.selectbox("Content type:", ["general", "creative", "technical"])
        
        if st.button("Generate Content"):
            if prompt:
                with st.spinner("Generating..."):
                    result = st.session_state.ml_app.generate_content(prompt, content_type)
                    if result:
                        st.success("Content generated!")
                        st.write(result)
            else:
                st.warning("Please enter a prompt.")
    
    elif task == "Data Insights":
        st.header("📊 Data Insights")
        
        data_desc = st.text_area("Describe your dataset:", height=150)
        
        if st.button("Get Insights"):
            if data_desc:
                with st.spinner("Analyzing data..."):
                    result = st.session_state.ml_app.data_insights(data_desc)
                    if result:
                        st.success("Insights generated!")
                        st.write(result)
            else:
                st.warning("Please describe your dataset.")
    
    elif task == "Code Analysis":
        st.header("💻 Code Analysis")
        
        code_input = st.text_area("Paste your code:", height=200)
        language = st.selectbox("Programming language:", ["python", "javascript", "java", "cpp", "other"])
        
        if st.button("Analyze Code"):
            if code_input:
                with st.spinner("Analyzing code..."):
                    result = st.session_state.ml_app.code_analysis(code_input, language)
                    if result:
                        st.success("Code analysis complete!")
                        st.write(result)
            else:
                st.warning("Please paste some code to analyze.")

if __name__ == "__main__":
    main()