# Docker Setup for local testing

---
# Important Note: You need the .env file, that the container will run.

## 1. Navigate to local folder

cd docker

## 2. Start/Create containers

docker compose --env-file .env up -d

## 3. Start chattrix-backend

Start the backend local in your IDE

## 4. Access application

- Backend: http://localhost:8080
- PHPMyAdmin: http://localhost:8090

---

## Stop containers

docker compose down