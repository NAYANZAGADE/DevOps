import boto3
import json
from botocore.exceptions import ClientError

# Initialize Bedrock client
bedrock_client = boto3.client('bedrock-runtime', region_name='us-east-1')

def invoke_claude(prompt, max_tokens=1000):
    """
    Invoke Claude 3 Sonnet model via Bedrock
    """
    model_id = "anthropic.claude-3-sonnet-20240229-v1:0"
    
    body = {
        "anthropic_version": "bedrock-2023-05-31",
        "max_tokens": max_tokens,
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ]
    }
    
    try:
        response = bedrock_client.invoke_model(
            modelId=model_id,
            body=json.dumps(body),
            contentType='application/json'
        )
        
        response_body = json.loads(response['body'].read())
        return response_body['content'][0]['text']
        
    except ClientError as e:
        print(f"Error invoking model: {e}")
        return None

def invoke_titan(prompt, max_tokens=512):
    """
    Invoke Amazon Titan Text model via Bedrock
    """
    model_id = "amazon.titan-text-express-v1"
    
    body = {
        "inputText": prompt,
        "textGenerationConfig": {
            "maxTokenCount": max_tokens,
            "temperature": 0.7,
            "topP": 0.9
        }
    }
    
    try:
        response = bedrock_client.invoke_model(
            modelId=model_id,
            body=json.dumps(body),
            contentType='application/json'
        )
        
        response_body = json.loads(response['body'].read())
        return response_body['results'][0]['outputText']
        
    except ClientError as e:
        print(f"Error invoking model: {e}")
        return None

# Example usage
if __name__ == "__main__":
    # Test with Claude
    claude_response = invoke_claude("Hello! Can you explain what AWS Bedrock is?")
    if claude_response:
        print("Claude says:")
        print(claude_response)
    
    print("\n" + "="*50 + "\n")
    
    # Test with Titan
    titan_response = invoke_titan("Write a short poem about cloud computing.")
    if titan_response:
        print("Titan says:")
        print(titan_response)