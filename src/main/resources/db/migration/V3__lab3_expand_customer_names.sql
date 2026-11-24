ALTER TABLE customers ADD COLUMN first_name VARCHAR(255);
ALTER TABLE customers ADD COLUMN last_name VARCHAR(255);

CREATE FUNCTION sync_customer_name_columns() RETURNS trigger AS $$
DECLARE
    normalized_name VARCHAR(255);
    separator_position INTEGER;
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.full_name IS NOT NULL THEN
            normalized_name := regexp_replace(btrim(NEW.full_name), '[[:space:]]+', ' ', 'g');
            separator_position := strpos(normalized_name, ' ');
            IF separator_position = 0 THEN
                NEW.first_name := normalized_name;
                NEW.last_name := '';
            ELSE
                NEW.first_name := substring(normalized_name FROM 1 FOR separator_position - 1);
                NEW.last_name := substring(normalized_name FROM separator_position + 1);
            END IF;
        ELSE
            NEW.full_name := concat_ws(' ', NULLIF(btrim(NEW.first_name), ''), NULLIF(btrim(NEW.last_name), ''));
        END IF;
    ELSIF NEW.full_name IS DISTINCT FROM OLD.full_name THEN
        normalized_name := regexp_replace(btrim(NEW.full_name), '[[:space:]]+', ' ', 'g');
        separator_position := strpos(normalized_name, ' ');
        IF separator_position = 0 THEN
            NEW.first_name := normalized_name;
            NEW.last_name := '';
        ELSE
            NEW.first_name := substring(normalized_name FROM 1 FOR separator_position - 1);
            NEW.last_name := substring(normalized_name FROM separator_position + 1);
        END IF;
    ELSIF NEW.first_name IS DISTINCT FROM OLD.first_name
            OR NEW.last_name IS DISTINCT FROM OLD.last_name THEN
        NEW.full_name := concat_ws(' ', NULLIF(btrim(NEW.first_name), ''), NULLIF(btrim(NEW.last_name), ''));
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_customer_name_columns
BEFORE INSERT OR UPDATE ON customers
FOR EACH ROW EXECUTE FUNCTION sync_customer_name_columns();
