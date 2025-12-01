#!/bin/bash

echo "🎵 Starting Spotify Clone Microservices..."
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "❌ .env file not found!"
    echo "Please copy .env.example to .env and add your Spotify credentials"
    echo ""
    echo "Get credentials from: https://developer.spotify.com/dashboard"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Build and start services
echo "🔨 Building Docker images..."
docker-compose build

echo ""
echo "🚀 Starting all services..."
docker-compose up -d

echo ""
echo "⏳ Waiting for services to be healthy..."
sleep 10

echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ All services started!"
echo ""
echo "🌐 Access Points:"
echo "  - 🎵 Frontend App:   http://localhost:8080"
echo ""
echo "  - Auth Service:      http://localhost:3001"
echo "  - User Service:      http://localhost:3002"
echo "  - Music Service:     http://localhost:3003"
echo "  - Streaming Service: http://localhost:3004"
echo "  - Search Service:    http://localhost:3005"
echo ""
echo "📊 Observability:"
echo "  - Jaeger (Traces):   http://localhost:16686"
echo "  - Prometheus:        http://localhost:9091"
echo "  - Grafana:           http://localhost:3000 (admin/admin)"
echo ""
echo "💾 Databases:"
echo "  - PostgreSQL:        localhost:5432"
echo "  - MongoDB:           localhost:27017"
echo "  - Redis:             localhost:6379"
echo ""
echo "📝 View logs: docker-compose logs -f [service-name]"
echo "🛑 Stop all:  docker-compose down"
