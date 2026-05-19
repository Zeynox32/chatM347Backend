# Chattrix Docker Setup

This project provides two Docker environments:

- Local
- Production

---
# Important Note: You need the .env files, that the containers will run.

# Start Local

## 1. Navigate to local folder

cd docker/local

## 2. Start the environment

docker compose --env-file .env up --build -d

## 3. Access application

- Backend: http://localhost:8080
- Database: localhost:3307
- PHPMyAdmin: http://localhost:8090

---

## Stop local environment

docker compose down

---

# Start Production

## 1. Build the application
./mvnw clean package -DskipTests in the root directory

## 2. Navigate to production folder

cd docker/production

## 3. Start production environment

docker compose --env-file .env up --build -d

## 4. Access application

- Backend: http://localhost:8080

---

## Stop production environment

docker compose down