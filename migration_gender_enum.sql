-- Migration script to update gender column for Enum compatibility
-- This script updates existing single-character gender values to match the new Gender enum

-- Step 1: Check current gender column type and values
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'users' AND column_name = 'gender';

SELECT DISTINCT gender, COUNT(*)
FROM users
GROUP BY gender;

-- Step 2: Alter column length if needed (PostgreSQL)
ALTER TABLE users ALTER COLUMN gender TYPE VARCHAR(10);

-- Step 3: No data conversion needed if values are already 'M' and 'F'
-- JPA will now map 'M' -> Gender.M and 'F' -> Gender.F automatically

-- Verification
SELECT user_id, name, gender, role FROM users LIMIT 5;
