CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS authors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    page_count INT NOT NULL,
    author_id UUID NOT NULL,
    CONSTRAINT fk_author
        FOREIGN KEY(author_id) 
        REFERENCES authors(id)
        ON DELETE CASCADE
);
