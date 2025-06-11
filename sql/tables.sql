CREATE TABLE IF NOT EXISTS authors (
    id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    page_count INT NOT NULL,
    author_id TEXT NOT NULL,
    CONSTRAINT fk_author
        FOREIGN KEY(author_id) 
        REFERENCES authors(id)
        ON DELETE CASCADE
);
