#!/bin/bash

GRAPHQL_URL="http://localhost:8080/graphql"

# Mutation: upsert author
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "mutation ($input: UpsertAuthorInput!) { upsertAuthor(input: $input) { id firstName lastName } }",
  "variables": { "input": { "id": "88bce345-c04e-4310-b7b9-d741f890bdb0", "firstName": "WAM", "lastName": "BO" } }
}' | jq .
