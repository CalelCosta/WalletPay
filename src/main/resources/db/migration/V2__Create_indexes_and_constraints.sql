-- Indexes for query optimization
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transfers_sender ON transfers(sender_transaction_id);
CREATE INDEX idx_transfers_receiver ON transfers(receiver_transaction_id);

-- Additional Constraints (if necessary)
-- Example: Ensure that the sender and receiver are different in transfers
ALTER TABLE transfers
    ADD CONSTRAINT check_sender_receiver_diff
        CHECK (sender_transaction_id != receiver_transaction_id);