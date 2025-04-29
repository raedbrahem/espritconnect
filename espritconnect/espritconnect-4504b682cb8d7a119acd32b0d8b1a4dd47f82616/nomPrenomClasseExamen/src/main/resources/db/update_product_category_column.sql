-- Update the category column in the product table to accommodate longer enum values
ALTER TABLE product MODIFY COLUMN category VARCHAR(30);
