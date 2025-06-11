#!/bin/bash

GRAPHQL_URL="http://localhost:8080/graphql"

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <bookId> <authorId>"
  exit 1
fi

BOOK_ID=$1
AUTHOR_ID=$2

curl -s -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "mutation ($input: UpsertBookInput!) { upsertBook(input: $input) { id name pageCount author { id firstName lastName } } }",
  "variables": { 
    "input": { 
      "id": "'"$BOOK_ID"'", 
      "name": "Updated Micronaut Book", 
      "pageCount": 350, 
      "authorId": "'"$AUTHOR_ID"'" 
    } 
  }
}' | jq .
