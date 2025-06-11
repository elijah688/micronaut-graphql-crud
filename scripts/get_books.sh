#!/bin/sh

GRAPHQL_URL="http://localhost:8080/graphql"

# Query: get books with pagination (first 5)
curl -X POST $GRAPHQL_URL -H "Content-Type: application/json" -d '{
  "query": "query ($first: Int) { books(first: $first) { edges { cursor node { id name pageCount author { id firstName lastName } } } pageInfo { startCursor endCursor hasNextPage hasPreviousPage } } }",
  "variables": { "first": 5 }
}' | jq .
