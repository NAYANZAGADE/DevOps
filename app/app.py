from flask import Flask, jsonify
import os
import psycopg2
import psycopg2.extras

app = Flask(__name__)

DB_HOST = os.environ.get("DB_HOST", "localhost")
DB_USER = os.environ.get("DB_USER", "appuser")
DB_PASS = os.environ.get("DB_PASS", "apppassword")
DB_NAME = os.environ.get("DB_NAME", "appdb")
DB_PORT = int(os.environ.get("DB_PORT", "5432"))

def get_conn():
    return psycopg2.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASS,
        dbname=DB_NAME,
        port=DB_PORT
    )

@app.route("/")
def index():
    return "Hello from AWS Flask app with PostgreSQL + ASG + ALB!\n"

@app.route("/health")
def health():
    try:
        conn = get_conn()
        conn.close()
        return jsonify(status="healthy")
    except Exception as e:
        return jsonify(status="unhealthy", error=str(e)), 500

@app.route("/users")
def users():
    try:
        conn = get_conn()
        cur = conn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        cur.execute("SELECT id, name, email FROM users LIMIT 100;")
        rows = cur.fetchall()
        conn.close()
        return jsonify([dict(r) for r in rows])
    except Exception as e:
        return jsonify(error=str(e)), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)


#!/bin/bash

sudo apt update -y
sudo apt install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ubuntu
sudo -u ubuntu docker pull nayanzagade7/gliding:latest
sudo -u ubuntu docker run -d -p 8000:8000 \
  -e DB_HOST=app-db.c1cy6wak2qua.ap-south-1.rds.amazonaws.com \
  -e DB_USER=postgres \
  -e DB_PASS=admin123 \
  -e DB_NAME=appdb \
  -e DB_PORT=5432 \
  nayanzagade7/gliding:latest
