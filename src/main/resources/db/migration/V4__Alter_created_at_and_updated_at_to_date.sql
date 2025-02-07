-- Alter Table Columns 'users'
ALTER TABLE users
ALTER COLUMN created_at TYPE DATE USING created_at::DATE,
    ALTER COLUMN created_at SET DEFAULT CURRENT_DATE,
    ALTER COLUMN updated_at TYPE DATE USING updated_at::DATE,
    ALTER COLUMN updated_at SET DEFAULT CURRENT_DATE;

-- Alter Table Column 'wallets'
ALTER TABLE wallets
ALTER COLUMN created_at TYPE DATE USING created_at::DATE,
    ALTER COLUMN created_at SET DEFAULT CURRENT_DATE,
    ALTER COLUMN updated_at TYPE DATE USING updated_at::DATE,
    ALTER COLUMN updated_at SET DEFAULT CURRENT_DATE;