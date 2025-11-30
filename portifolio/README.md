# Nayan Zagade - Cloud DevOps Engineer Portfolio

A modern 3D portfolio website built with microservice architecture, featuring React with Three.js for 3D animations, Node.js backend API, and Nginx reverse proxy.

## Architecture

This project follows a microservice architecture with three main services:

- **Frontend**: React app with Three.js for 3D graphics and Framer Motion for animations
- **Backend**: Node.js/Express REST API
- **Nginx**: Reverse proxy for routing requests

## Tech Stack

- **Frontend**: React, Three.js, @react-three/fiber, @react-three/drei, Framer Motion
- **Backend**: Node.js, Express
- **Containerization**: Docker, Docker Compose
- **Web Server**: Nginx

## Prerequisites

- Docker
- Docker Compose
- Node.js (for local development)

## Quick Start

### Option 1: Using Start Script (Recommended for Development)
```bash
./start.sh
```

### Option 2: Manual Start
```bash
# Terminal 1 - Backend
cd backend
npm install
npm start

# Terminal 2 - Frontend
cd frontend
npm install
npm start
```

### Option 3: Using Docker
```bash
docker compose up --build
```

Access the application:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:5000
- **Nginx (Docker only)**: http://localhost

## Development

### Frontend Development
```bash
cd frontend
npm install
npm start
```

### Backend Development
```bash
cd backend
npm install
npm run dev
```

## Docker Services

- **frontend**: React app served via Nginx (port 3000)
- **backend**: Express API (port 5000)
- **nginx**: Reverse proxy (port 80)

## API Endpoints

- `GET /api/portfolio` - Get portfolio data
- `GET /api/health` - Health check

## Features

✨ **3D DevOps-Themed Background**
- Floating tech stack icons (Kubernetes, Docker, Jenkins, Git, GitHub, ArgoCD, Linux, Terraform, AWS, Ansible)
- Interactive 3D animations with Three.js
- Auto-rotating camera with particle effects
- CI/CD pipeline visualization

🎨 **Modern UI/UX**
- Glassmorphism design with backdrop blur effects
- Smooth scroll animations with Framer Motion
- Gradient text effects and hover animations
- Responsive navigation bar
- Interactive skill tags with ripple effects

🏗️ **Architecture**
- Microservice architecture (Frontend, Backend, Nginx)
- RESTful API with Express
- Containerized with Docker
- Docker Compose orchestration
- Reverse proxy setup

## Project Structure

```
.
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── App.js
│   │   └── index.js
│   ├── Dockerfile
│   └── package.json
├── backend/
│   ├── server.js
│   ├── Dockerfile
│   └── package.json
├── nginx/
│   ├── nginx.conf
│   └── Dockerfile
└── docker-compose.yml
```

## Contact

- Email: nayanzagade7@gmail.com
- Phone: 9960936078
- LinkedIn: [nayan-zagade-9152b1271](https://linkedin.com/in/nayan-zagade-9152b1271)
- GitHub: [NAYANZAGADE](https://github.com/NAYANZAGADE)
