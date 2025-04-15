-- Drop the existing foreign key constraint
ALTER TABLE payments 
DROP FOREIGN KEY FK81gagumt0r8y3rmudcgpbk42l;

-- Recreate the constraint with ON DELETE CASCADE
ALTER TABLE payments
ADD CONSTRAINT FK81gagumt0r8y3rmudcgpbk42l
FOREIGN KEY (order_id) 
REFERENCES orders(id_order)
ON DELETE CASCADE;
