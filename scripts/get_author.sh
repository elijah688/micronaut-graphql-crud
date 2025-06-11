#!/bin/sh

GRAPHQL_URL="http://localhost:8080/graphql"

# Query: get author by ID
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "query ($id: ID!) { authorById(id: $id) { id firstName lastName } }",
  "variables": { "id": "88bce345-c04e-4310-b7b9-d741f890bdb0" }
}' | jq .
