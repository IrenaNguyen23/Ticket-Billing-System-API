# Train Ticket System (Backend)

## Quick Start (Docker)
1. Ensure Docker Desktop is running.
2. From repo root:

```bash
docker-compose up --build
```

Services will be available on:
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- Trip Service: http://localhost:8082
- Billing Service: http://localhost:8083
- Payment Service: http://localhost:8084

## JWT Secret
All services use `security.jwt.secret`. The default is set in config, but for production you must override with a 32+ byte secret.

Example (PowerShell):
```powershell
$env:SECURITY_JWT_SECRET="your-32+byte-secret"; docker-compose up --build
```

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