# RTD-Denver Service Reliability

Measure RTD-Denver service reliability by comparing scheduled arrivals (GTFS) with real-time arrivals (GTFS-Realtime) and calculating delay deltas for selected rail and bus routes.

**Example insight:**
> Route X is late by more than 5 minutes 3x per weekday

---

## Mission
Analyze how closely RTD services adhere to their published schedules by computing delay deltas between static GTFS data and real-time GTFS-Realtime updates.

The goal is to produce simple reliability metrics that reflect the rider experience.

---

## Data Sources
- **GTFS (static):** routes, trips, stops, stop times, and service calendars
- **GTFS-Realtime:** live trip updates containing arrival and departure predictions

---

## Implementation Overview

### 1. Load GTFS Static Data
- Import GTFS CSV files into PostgreSQL
- Key datasets:
    - `routes`
    - `trips`
    - `stops`
    - `stop_times`
    - `calendar` / `calendar_dates`
- Filter trips that are active on the current service day

---

### 2. Poll GTFS-Realtime Feeds
- Periodically fetch GTFS-Realtime Trip Updates protocol buffer files
- Decode protobuf payloads using the GTFS-Realtime schema
- Focus on `trip_update.stop_time_update` records

---

### 3. Calculate Delay Deltas
- Match real-time updates to scheduled stop times using:
    - `trip_id`
    - `stop_id`
- Compute delay
