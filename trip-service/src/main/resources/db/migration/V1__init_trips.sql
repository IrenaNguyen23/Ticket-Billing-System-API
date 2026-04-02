CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS stations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(10) UNIQUE NOT NULL,
    name        VARCHAR(255) NOT NULL,
    line        VARCHAR(50),
    zone        VARCHAR(10),
    latitude    DECIMAL(10, 8),
    longitude   DECIMAL(11, 8),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS trips (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL,
    checkin_station_id    UUID REFERENCES stations(id) NOT NULL,
    checkout_station_id   UUID REFERENCES stations(id),
    checkin_time          TIMESTAMP NOT NULL,
    checkout_time         TIMESTAMP,
    duration_minutes      INTEGER,
    status                VARCHAR(30) DEFAULT 'ACTIVE',
    created_at            TIMESTAMP DEFAULT NOW(),
    updated_at            TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_trips_user_id ON trips(user_id);
CREATE INDEX IF NOT EXISTS idx_trips_status ON trips(status);
CREATE INDEX IF NOT EXISTS idx_trips_checkin_time ON trips(checkin_time);
