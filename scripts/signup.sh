#!/bin/sh

curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
        "username": "johndoe",
        "password": "secret123"
      }' | jq .
