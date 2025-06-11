#!/bin/bash

GRAPHQL_URL="http://localhost:8080/graphql"

if [ -z "$1" ]; then
  echo "Usage: $0 <authorId>"
  exit 1
fi

AUTHOR_ID=$1

curl -s -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "mutation ($input: UpsertBookInput!) { upsertBook(input: $input) { id name pageCount author { id firstName lastName } } }",
  "variables": { 
    "input": { 
      "name": "Micronaut in Action", 
      "pageCount": 300, 
      "authorId": "'"$AUTHOR_ID"'" 
    } 
  }
}' | jq .
