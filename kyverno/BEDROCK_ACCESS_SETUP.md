# AWS Bedrock Model Access Setup

You're getting this error because you need to request access to Anthropic's Claude models in AWS Bedrock. Here's how to fix it:

## 🚀 Quick Fix (5 minutes)

### Step 1: Go to AWS Bedrock Console
1. Open [AWS Bedrock Console](https://console.aws.amazon.com/bedrock/)
2. Make sure you're in the correct region (us-east-1 recommended)

### Step 2: Request Model Access
1. In the left sidebar, click **"Model access"**
2. Click **"Request model access"** button
3. Find **"Anthropic"** section
4. Check the box for **"Claude 3 Sonnet"**
5. Click **"Request model access"**

### Step 3: Fill Out Use Case Form
1. You'll see a form asking about your use case
2. Fill out the required fields:
   - **Use case description**: "Building ML applications for text analysis and content generation"
   - **Industry**: Select your industry
   - **Company size**: Select appropriate size
3. Submit the form

### Step 4: Wait for Approval
- **Most regions**: Instant approval ✅
- **Some regions**: Up to 15 minutes ⏰
- **Rare cases**: Up to 24 hours 📅

## 🔄 Alternative: Use Titan Model (Available Now)

While waiting for Claude access, your app will automatically fallback to Amazon Titan. You can also manually use Titan:

```python
# Test with Titan (should work immediately)
python cli_ml_app.py --task generate --text "Hello world" --type creative
```

## 🌍 Region-Specific Access

Different models are available in different regions:

| Region | Claude 3 Sonnet | Titan Text |
|--------|----------------|------------|
| us-east-1 | ✅ | ✅ |
| us-west-2 | ✅ | ✅ |
| eu-west-1 | ✅ | ✅ |
| ap-southeast-1 | ✅ | ✅ |

## 🧪 Test Your Access

### Check Available Models
```bash
aws bedrock list-foundation-models --region us-east-1
```

### Test Claude Access
```python
python -c "
import boto3
import json

client = boto3.client('bedrock-runtime', region_name='us-east-1')
try:
    response = client.invoke_model(
        modelId='anthropic.claude-3-sonnet-20240229-v1:0',
        body=json.dumps({
            'anthropic_version': 'bedrock-2023-05-31',
            'max_tokens': 100,
            'messages': [{'role': 'user', 'content': 'Hello!'}]
        }),
        contentType='application/json'
    )
    print('✅ Claude access working!')
except Exception as e:
    print(f'❌ Claude access issue: {e}')
"
```

### Test Titan Access
```python
python -c "
import boto3
import json

client = boto3.client('bedrock-runtime', region_name='us-east-1')
try:
    response = client.invoke_model(
        modelId='amazon.titan-text-express-v1',
        body=json.dumps({
            'inputText': 'Hello!',
            'textGenerationConfig': {'maxTokenCount': 100}
        }),
        contentType='application/json'
    )
    print('✅ Titan access working!')
except Exception as e:
    print(f'❌ Titan access issue: {e}')
"
```

## 🔧 Update Your App Configuration

If you want to use a different region or model, update `ml_app.py`:

```python
# Change region
def __init__(self, region_name='us-west-2'):  # Change region here

# Or use different models
self.models = {
    'claude': "anthropic.claude-3-haiku-20240307-v1:0",  # Faster, cheaper
    'titan': "amazon.titan-text-express-v1",
    'jurassic': "ai21.j2-ultra-v1"
}
```

## 📞 Still Having Issues?

### Common Problems:

**1. Wrong Region**
- Make sure you're requesting access in the same region your app uses
- Default is `us-east-1`

**2. Account Limits**
- Some AWS accounts have restrictions
- Contact AWS support if needed

**3. Corporate Account**
- Your organization might need to approve Bedrock usage
- Check with your AWS administrator

### Get Help:
1. Check [AWS Bedrock Documentation](https://docs.aws.amazon.com/bedrock/)
2. Contact AWS Support
3. Check AWS Service Health Dashboard

## 🎉 Once Access is Approved

Your app will automatically start using Claude! The fallback to Titan will stop, and you'll get better results for complex analysis tasks.

**Pro tip**: Claude is better for analysis and reasoning, while Titan is great for creative content generation. Your app uses both strategically! 🚀