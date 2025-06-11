#!/bin/sh

GRAPHQL_URL="http://localhost:8080/graphql"

# Query: get author by ID
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "query ($id: ID!) { authorById(id: $id) { id firstName lastName } }",
  "variables": { "id": "ba609616-3f98-4477-ab27-3bc8498211fd" }
}' | jq .
