#!/bin/sh

curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query($first: Int, $after: String) { books(first: $first, after: $after) { edges { cursor node { id name } } pageInfo { startCursor endCursor hasNextPage hasPreviousPage } } }",
    "variables": {
      "first": 2,
      "after": null
    }
  }'

