#!/bin/bash
echo "Setting up AI Stock Trader project..."

# Backend
cd backend
./mvnw clean install
cd ..

# AI Service
cd ai-service
pip install -r requirements.txt
cd ..

echo "Setup complete!"
