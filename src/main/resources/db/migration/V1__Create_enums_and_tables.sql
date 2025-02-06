-- Types ENUM
CREATE TYPE transaction_type AS ENUM ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER');
CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED');

-- Table 'users'
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 'wallets'
CREATE TABLE wallets (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                         balance DECIMAL(15, 2) DEFAULT 0.00 CHECK (balance >= 0),
                         currency VARCHAR(3) DEFAULT 'BRL',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 'transactions'
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              wallet_id INT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                              amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
                              type transaction_type NOT NULL,
                              status transaction_status DEFAULT 'PENDING',
                              description VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 'transfers'
CREATE TABLE transfers (
                           id SERIAL PRIMARY KEY,
                           sender_transaction_id INT NOT NULL REFERENCES transactions(id),
                           receiver_transaction_id INT NOT NULL REFERENCES transactions(id),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);