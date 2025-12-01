#!/usr/bin/env python3
"""
Check AWS Bedrock model access and availability
"""

import boto3
import json
from botocore.exceptions import ClientError

def check_model_access(region='us-east-1'):
    """Check access to different Bedrock models"""
    client = boto3.client('bedrock-runtime', region_name=region)
    
    models_to_test = {
        'Claude 3 Sonnet': 'anthropic.claude-3-sonnet-20240229-v1:0',
        'Claude 3 Haiku': 'anthropic.claude-3-haiku-20240307-v1:0',
        'Titan Text Express': 'amazon.titan-text-express-v1',
        'AI21 Jurassic': 'ai21.j2-ultra-v1'
    }
    
    print(f"🔍 Checking Bedrock model access in {region}")
    print("=" * 50)
    
    for model_name, model_id in models_to_test.items():
        try:
            if 'claude' in model_id.lower():
                # Test Claude models
                body = {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 10,
                    "messages": [{"role": "user", "content": "Hi"}]
                }
            elif 'titan' in model_id.lower():
                # Test Titan models
                body = {
                    "inputText": "Hi",
                    "textGenerationConfig": {"maxTokenCount": 10}
                }
            else:
                # Test other models
                body = {
                    "prompt": "Hi",
                    "maxTokens": 10
                }
            
            response = client.invoke_model(
                modelId=model_id,
                body=json.dumps(body),
                contentType='application/json'
            )
            
            print(f"✅ {model_name}: Access granted")
            
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == 'ResourceNotFoundException':
                if 'use case details' in str(e):
                    print(f"⚠️  {model_name}: Need to request access (fill use case form)")
                else:
                    print(f"❌ {model_name}: Model not available in this region")
            elif error_code == 'AccessDeniedException':
                print(f"🔒 {model_name}: Access denied (check permissions)")
            else:
                print(f"❓ {model_name}: {error_code}")
        except Exception as e:
            print(f"❌ {model_name}: Unexpected error - {e}")

def list_available_models(region='us-east-1'):
    """List all available foundation models"""
    try:
        client = boto3.client('bedrock', region_name=region)
        response = client.list_foundation_models()
        
        print(f"\n📋 Available models in {region}:")
        print("-" * 30)
        
        for model in response['modelSummaries']:
            print(f"• {model['modelName']} ({model['modelId']})")
            
    except Exception as e:
        print(f"❌ Error listing models: {e}")

def main():
    print("🤖 AWS Bedrock Access Checker")
    print("=" * 40)
    
    # Check default region
    region = 'us-east-1'
    check_model_access(region)
    
    # List available models
    list_available_models(region)
    
    print("\n💡 Next steps:")
    print("1. If you see '⚠️ Need to request access', go to AWS Bedrock console")
    print("2. Click 'Model access' → 'Request model access'")
    print("3. Select the models you need and fill out the use case form")
    print("4. Wait for approval (usually instant)")
    print("\n🔗 AWS Bedrock Console: https://console.aws.amazon.com/bedrock/")

if __name__ == "__main__":
    main()