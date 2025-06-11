#!/bin/bash

GRAPHQL_URL="http://localhost:8080/graphql"

# Mutation: upsert author
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "mutation ($input: UpsertAuthorInput!) { upsertAuthor(input: $input) { id firstName lastName } }",
  "variables": { "input": { "id": "author-1", "firstName": "John", "lastName": "Doe" } }
}' | jq .
