#!/bin/sh

GRAPHQL_URL="http://localhost:8080/graphql"

# Query: get book by ID
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "query ($id: ID) { bookById(id: $id) { id name pageCount author { id firstName lastName } } }",
  "variables": { "id": "book-1" }
}' | jq .
