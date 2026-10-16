CREATE EXTENSION IF NOT EXISTS citext;

-- Create ACCOUNT table
CREATE TABLE account(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email CITEXT UNIQUE NOT NULL                        -- For the purposes of this project, service layer will verify if this is a valid email
        CHECK(length(btrim(email)) > 0),
    first_name TEXT NOT NULL
        CHECK(length(btrim(first_name)) > 0),
    last_name TEXT NOT NULL
        CHECK(length(btrim(last_name)) > 0),
    password TEXT NOT NULL                              -- Account will store encoded password, not plain-text password
        CHECK(length(password) > 0),
    balance_in_cents BIGINT NOT NULL DEFAULT 0          -- Account balance will be 0 by default and can't go negative
        CHECK(balance_in_cents >= 0) 
);

-- Create BANK_TRANSACTION table
CREATE TABLE bank_transaction (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type TEXT NOT NULL                                  -- Indicates bank transaction type; limited to specific values
        CHECK(type IN (
            'deposit', 'withdrawal', 'transfer'
        )),
    amount_in_cents BIGINT NOT NULL                     -- Bank transaction amount must be positive
        CHECK(amount_in_cents > 0),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    memo TEXT,                                          -- Free-form notes about the bank transaction

    -- Account ID foreign keys are to be NULL or NOT NULL depending on bank transaction type
    -- Additionally, for the purposes of this project, we block attempts to delete accounts associated with this bank transaction
    sender_account_id BIGINT REFERENCES account(id)     -- Sender account ID column for transfers, account ID column for withdrawals
        ON DELETE RESTRICT,
    recipient_account_id BIGINT REFERENCES account(id)  -- Recipient account ID column for transfers, account ID column for deposits
        ON DELETE RESTRICT,

    -- Ensures that account ID foreign keys are NULLed / NOT NULLed properly according to bank transaction type
    CONSTRAINT account_id_foreign_keys_match_type CHECK (
        CASE
            -- Deposit
            WHEN type='deposit'
                THEN sender_account_id IS NULL
                AND recipient_account_id IS NOT NULL

            -- Withdrawal
            WHEN type='withdrawal'
                THEN sender_account_id IS NOT NULL
                AND recipient_account_id IS NULL

            -- Transfer
            WHEN type='transfer'
                THEN sender_account_id IS NOT NULL
                AND recipient_account_id IS NOT NULL
            
            ELSE FALSE
        END
    ),

    -- Ensures that sender and recipient account IDs are not equal for transfers
    -- Ignored for non-transfers
    CONSTRAINT sender_account_id_and_recipient_account_id_not_equal_for_transfers CHECK (
        -- Non-transfers
        type <> 'transfer' OR

        -- Transfer
        sender_account_id <> recipient_account_id
    ),

    -- Ensures that deposit and transfer amounts do not exceed limit of $10,000
    CONSTRAINT deposit_or_transfer_amount_at_most_max_limit CHECK (
        -- Not deposit/transfer
        type NOT IN ('deposit', 'transfer') OR

        -- Deposit/transfer
        amount_in_cents <= 1000000
    )
);

-- Optimize BANK_TRANSACTION lookups by:
CREATE INDEX bank_transaction_sender_account_id_idx     -- Sender account ID
ON bank_transaction(sender_account_id)
WHERE sender_account_id IS NOT NULL;
CREATE INDEX bank_transaction_recipient_account_id_idx  -- Recipient account ID
ON bank_transaction(recipient_account_id)
WHERE recipient_account_id IS NOT NULL;
