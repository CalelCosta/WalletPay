-- Add Column CPF
ALTER TABLE users
    ADD COLUMN cpf VARCHAR(14) UNIQUE;
COMMENT ON COLUMN users.cpf IS 'User CPF (format: 123.456.789-10)';