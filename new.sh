#!/bin/sh

curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation($input: UpsertBookInput!) { upsertBook(input: $input) { id name pageCount author { id firstName lastName } } }",
    "variables": {
      "input": {
        "id": "book-1",
        "name": "New Book Title",
        "pageCount": 3333,
        "authorId": "author-1"
      }
    }
  }'
