# Train Ticket System (Backend)

## Quick Start (Docker)
1. Ensure Docker Desktop is running.
2. From repo root:

```bash
docker-compose up --build
```

## Run Local (no Docker)
Prereqs: Postgres, Redis, Kafka running locally.

```powershell
.\run-local.ps1
```

Optional: skip gateway
```powershell
.\run-local.ps1 -NoGateway
```

`run-local.ps1` loads `.env.local` if present; otherwise it falls back to `.env`.

## Environment Variables (.env)
All runtime variables are centralized in `.env`. Update this file to change ports, DB URLs, JWT secret, Kafka, Redis, and gateway routing.

Key variables:
- `SECURITY_JWT_SECRET` (must be 32+ bytes)
- `AUTH_DB_URL`, `TRIP_DB_URL`, `BILLING_DB_URL`, `PAYMENT_DB_URL`
- `REDIS_HOST`, `KAFKA_BOOTSTRAP_SERVERS`
- `AUTH_SERVICE_URL`, `TRIP_SERVICE_URL`, `BILLING_SERVICE_URL`, `PAYMENT_SERVICE_URL`

## New Relic APM
Each service image bundles the New Relic Java agent (downloaded from New Relic’s official URL) and starts the JVM with `-javaagent:/opt/newrelic/newrelic.jar`.

Configuration is controlled via environment variables in `.env` (env vars override `newrelic.yml`).

Required:
- `NEW_RELIC_LICENSE_KEY`
- `NEW_RELIC_APP_NAME_*` (per service)

Optional:
- `NEW_RELIC_LOG_FILE_NAME=STDOUT`
- `NEW_RELIC_AGENT_ENABLED=true|false`

To disable APM in a given environment, set `NEW_RELIC_AGENT_ENABLED=false` in `.env`.

Services will be available on:
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- Trip Service: http://localhost:8082
- Billing Service: http://localhost:8083
- Payment Service: http://localhost:8084

## JWT Secret
Set `SECURITY_JWT_SECRET` in `.env` to a 32+ byte value before running in production.

## Sample cURL

### Register
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"Passw0rd!","fullName":"User One"}'
```

### Login (get access token)
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"Passw0rd!"}'
```

### Check-in
```bash
curl -s -X POST http://localhost:8080/api/v1/trips/checkin \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"stationId":"<STATION_UUID>"}'
```

### Check-out
```bash
curl -s -X POST http://localhost:8080/api/v1/trips/checkout \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"stationId":"<STATION_UUID>"}'
```

### Top-up
```bash
curl -s -X POST http://localhost:8080/api/v1/payments/topup \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"amount":100000}'
```

### Balance
```bash
curl -s -X GET http://localhost:8080/api/v1/payments/balance \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Notes
- `stationId` must exist in `trip_db.stations`. Seed data is not included yet.
- Check-in and check-out require a valid JWT access token.
  ## remove cache
-docker builder prune 
