DROP TRIGGER trg_sync_customer_name_columns ON customers;
DROP FUNCTION sync_customer_name_columns();
ALTER TABLE customers DROP COLUMN full_name;
