#!/bin/bash

GRAPHQL_URL="http://localhost:8080/graphql"
LOGIN_URL="http://localhost:8080/auth/login"

USERNAME="johndoe"
PASSWORD="secret123"

# Step 1: Login and get JWT token
LOGIN_RESPONSE=$(curl -s -X POST $LOGIN_URL \
  -H "Content-Type: application/json" \
  -d "{
        \"username\": \"$USERNAME\",
        \"password\": \"$PASSWORD\"
      }")

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.access_token')
echo "Got token: $TOKEN"

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "Login failed or token not found"
  exit 1
fi

echo "Got token: $TOKEN"

# Step 2: Call GraphQL mutation with token
curl -X POST $GRAPHQL_URL \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
        "query": "mutation ($input: UpsertAuthorInput!) { upsertAuthor(input: $input) { id firstName lastName } }",
        "variables": { "input": { "id": "88bce345-c04e-4310-b7b9-d741f890bdb0", "firstName": "WAM", "lastName": "BO" } }
      }' | jq .
