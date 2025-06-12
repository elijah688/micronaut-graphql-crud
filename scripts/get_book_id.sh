#!/bin/sh

GRAPHQL_URL="http://localhost:8080/graphql"

# Query: get book by ID
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "query ($id: ID) { bookById(id: $id) { id name pageCount author { id firstName lastName } } }",
  "variables": { "id": "30b63ced-c68c-4cba-88a4-e1dac123311b" }
}' | jq .
