# RTD-Denver Service Reliability


This backend service measures on time performance (OTP) for RTD Denver transit by comparing scheduled GTFS arrivals with GTFS-Realtime arrival predictions.

The app is currently focused on **Bus Route 15 arrivals at Union Station** and demonstrates how real-time transit reliability metrics can be computed from public GTFS data.

## What this app can do (current scope)
- Tracks Route 15 bus arrivals at Union Station
- Computes arrival delay (seconds) using:
    - [GTFS static schedule](https://www.rtd-denver.com/open-records/open-spatial-information/gtfs)
    - [GTFS Realtime trip updates](https://www.rtd-denver.com/open-records/open-spatial-information/real-time-feeds) 
- Stores one row per trip arrival (no duplicate polling noise)
- Calculates on-time performance (±5 minutes)

⚠️ Note: Statistics are based on a limited sample window and are meant to demonstrate capability, not to draw definitive conclusions about RTD reliability.

## On Time Performance Definition
An arrival is classified as
- **Early**: more than 5 minutes early
- **On time**: within ±5 minutes of scheduled arrival
- **Late**: more than 5 minutes late

### Example Response
```json
{
  "total": 39,
  "early": 4,
  "onTime": 24,
  "late": 11,
  "onTimePct": 61.5
}
```

## API Endpoint

### Get Lifetime on time performance
```
GET http://localhost:8080/otp-lifetime
```
Returns aggregate on time statistics for Route 15 at Union Station.

Stop IDs and route configuration are handled server side, no query parameters required.

## Data sources
- [GTFS static](https://www.rtd-denver.com/open-records/open-spatial-information/gtfs)
  - routes, trips, stops, stop_times, calendars
- [GTFS Realtime](https://www.rtd-denver.com/open-records/open-spatial-information/real-time-feeds)
    - Trip Updates (protobuf)

## Implementation Overview

### 1. Load GTFS Static Data
- Import GTFS CSV files into PostgreSQL
- Key datasets:
    - `routes`
    - `trips`
    - `stops`
    - `stop_times`
    - `calendar` / `calendar_dates`
- Identify trips active on the current service day
- Resolve scheduled arrival times per `(trip_id, stop_id)`

### 2. Poll GTFS-Realtime Feeds
- Poll GTFS-Realtime Trip Updates every 15 seconds
- Decode protobuf payloads using the GTFS-Realtime schema
- Filter to Route 15 and Union Station stop IDs
- Focus on `trip_update.stop_time_update` records

### 3. Calculate Delay Deltas
- Match real-time arrivals to scheduled stop times using:
    - `trip_id`
    - `stop_id`
- Compute delay in seconds:
```
delay_seconds = realtime_arrival - scheduled_arrival
```
- Upsert by `(trip_id, stop_id)` to avoid duplicate samples

### 4. Compute Reliability Metrics
- Classify each arrival as:
- Early
- On-time (±5 minute window)
- Late
- Aggregate metrics directly in PostgreSQL

## Environment Config
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rtd
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL-AUTO=update

RTD.ROUTE-ID=15
RTD.UNION-STOP-NAME-LIKE=%union%
```

## Pre-Startup Setup (GTFS Static Import)
Before running the Spring Boot app, you need to create the GTFS tables and import the GTFS static files into PostgreSQL.

### Scripts Location
All required setup commands are documented in:
```
scripts/
create-gtfs-tables.sql
import-gtfs.ps1
```

### Steps
1. Download and unzip the RTD GTFS static feed
2. Review the scripts in `rtd-reliability/scripts`
3. Run the import script as documented in that directory