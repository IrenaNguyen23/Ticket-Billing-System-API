CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS billing_records (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id     UUID UNIQUE NOT NULL,
    user_id     UUID NOT NULL,
    amount      BIGINT NOT NULL,
    currency    VARCHAR(10) DEFAULT 'VND',
    zone_from   VARCHAR(10),
    zone_to     VARCHAR(10),
    fare_source VARCHAR(30),
    status      VARCHAR(20) DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT NOW()
);
