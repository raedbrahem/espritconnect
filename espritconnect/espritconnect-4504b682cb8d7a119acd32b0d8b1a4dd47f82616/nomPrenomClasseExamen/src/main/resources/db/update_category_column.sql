-- Update the category column in the item table to accommodate longer enum values
ALTER TABLE item MODIFY COLUMN category VARCHAR(20);
