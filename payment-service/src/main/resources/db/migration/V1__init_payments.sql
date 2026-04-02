CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS wallets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID UNIQUE NOT NULL,
    balance     BIGINT DEFAULT 0 NOT NULL,
    currency    VARCHAR(10) DEFAULT 'VND',
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    wallet_id       UUID REFERENCES wallets(id),
    trip_id         UUID,
    billing_id      UUID,
    amount          BIGINT NOT NULL,
    type            VARCHAR(10) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    failure_reason  TEXT,
    reference_id    VARCHAR(255),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
