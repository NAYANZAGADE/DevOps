#!/bin/bash

echo "🧪 Testing Spotify Clone Services..."
echo ""

# Test health endpoints
services=("auth-service:3001" "user-service:3002" "music-service:3003" "streaming-service:3004" "search-service:3005")

for service in "${services[@]}"; do
    name=$(echo $service | cut -d: -f1)
    port=$(echo $service | cut -d: -f2)
    
    echo -n "Testing $name... "
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/health)
    
    if [ "$response" = "200" ]; then
        echo "✅ Healthy"
    else
        echo "❌ Failed (HTTP $response)"
    fi
done

echo ""
echo "🔐 Testing Auth Flow..."
echo ""

# Register a test user
echo "1. Registering user..."
register_response=$(curl -s -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}')

echo "Response: $register_response"
echo ""

# Login
echo "2. Logging in..."
login_response=$(curl -s -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}')

echo "Response: $login_response"
echo ""

# Extract token
token=$(echo $login_response | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$token" ]; then
    echo "✅ Got auth token!"
    echo ""
    
    # Test authenticated endpoint
    echo "3. Testing authenticated endpoint (User Profile)..."
    profile_response=$(curl -s -X GET http://localhost:3002/api/users/profile \
      -H "Authorization: Bearer $token")
    
    echo "Response: $profile_response"
else
    echo "❌ Failed to get token"
fi

echo ""
echo "✅ Testing complete!"
