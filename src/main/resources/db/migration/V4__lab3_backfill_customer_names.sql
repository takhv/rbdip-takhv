UPDATE customers
SET first_name = CASE
        WHEN strpos(normalized_name, ' ') = 0 THEN normalized_name
        ELSE substring(normalized_name FROM 1 FOR strpos(normalized_name, ' ') - 1)
    END,
    last_name = CASE
        WHEN strpos(normalized_name, ' ') = 0 THEN ''
        ELSE substring(normalized_name FROM strpos(normalized_name, ' ') + 1)
    END
FROM (
    SELECT id, regexp_replace(btrim(full_name), '[[:space:]]+', ' ', 'g') AS normalized_name
    FROM customers
) AS normalized
WHERE customers.id = normalized.id;

ALTER TABLE customers ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE customers ALTER COLUMN last_name SET NOT NULL;
