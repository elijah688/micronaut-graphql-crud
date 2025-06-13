-- Suppose the cursor is:
-- created_at_cursor = '2025-06-13 19:04:20.702819+00'
-- id_cursor = '480d5cdc-96c1-4194-83d2-2b38ba98a220'
-- books(first: 5, after: (created_at_cursor, id_cursor))
SELECT id, created_at, name
FROM books
WHERE (created_at, id) > ('2025-06-13 19:04:20.702819+00', '480d5cdc-96c1-4194-83d2-2b38ba98a220')
ORDER BY created_at, id
LIMIT 5;


-- Suppose the cursor is:
-- created_at_cursor = '2025-06-13 19:04:20.702819+00'
-- id_cursor = '480d5cdc-96c1-4194-83d2-2b38ba98a220'
-- books(last: 5, before: (created_at_cursor, id_cursor))
SELECT id, created_at, name
FROM books
WHERE (created_at, id) < ('2025-06-13 19:04:20.702819+00', '480d5cdc-96c1-4194-83d2-2b38ba98a220')
ORDER BY created_at DESC, id DESC
LIMIT 5;
