# Spotify Clone - Microservices Architecture

A production-ready Spotify clone built with microservices architecture, designed for Kubernetes and AWS deployment with full observability (metrics, logs, traces).

## Architecture Overview

### Microservices
1. **auth-service** - User authentication & authorization (JWT)
2. **user-service** - User profile management
3. **music-service** - Track, album, playlist management
4. **streaming-service** - Audio streaming & playback
5. **search-service** - Search functionality across music catalog

### Tech Stack
- **Backend**: Node.js/Express
- **Databases**: PostgreSQL (auth, user, music), MongoDB (search), Redis (caching)
- **Observability**: OpenTelemetry, Prometheus, Grafana, Jaeger
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Cloud**: AWS (EKS, RDS, ElastiCache)

### Spotify API Integration
All services integrate with real Spotify Web API for music data and streaming.

## Quick Start

```bash
# Start all services
./start.sh

# Or manually
docker compose up -d --build

# Access the app
open http://localhost:8080
```

## Access the Application

- **Frontend**: http://localhost:8080
- **Jaeger (Traces)**: http://localhost:16686
- **Prometheus (Metrics)**: http://localhost:9091
- **Grafana (Dashboards)**: http://localhost:3000 (admin/admin)

## Environment Variables
Each service requires Spotify API credentials. See individual service READMEs.
