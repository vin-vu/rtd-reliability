# path to unzipped GTFS folder (adjacent to project root)
$GtfsDir = Resolve-Path "..\google_transit"

if (-not (Test-Path $GtfsDir)) {
  Write-Error "GTFS directory not found: $GtfsDir"
  exit 1
}

# database connection - set this once in shell
# $env:DATABASE_URL="your-postgres"

Write-Host "Importing GTFS from $GtfsDir"

psql "$env:DATABASE_URL" -v ON_ERROR_STOP=1 -c @"
TRUNCATE TABLE
  gtfs_stop_times,
  gtfs_trips,
  gtfs_routes,
  gtfs_stops,
  gtfs_calendar,
  gtfs_calendar_dates;
"@

psql "$env:DATABASE_URL" -c "\copy gtfs_stops          FROM '$GtfsDir\stops.txt'          WITH (FORMAT csv, HEADER true)"
psql "$env:DATABASE_URL" -c "\copy gtfs_routes         FROM '$GtfsDir\routes.txt'         WITH (FORMAT csv, HEADER true)"
psql "$env:DATABASE_URL" -c "\copy gtfs_trips          FROM '$GtfsDir\trips.txt'          WITH (FORMAT csv, HEADER true)"
psql "$env:DATABASE_URL" -c "\copy gtfs_stop_times     FROM '$GtfsDir\stop_times.txt'     WITH (FORMAT csv, HEADER true)"
psql "$env:DATABASE_URL" -c "\copy gtfs_calendar       FROM '$GtfsDir\calendar.txt'       WITH (FORMAT csv, HEADER true)"
psql "$env:DATABASE_URL" -c "\copy gtfs_calendar_dates FROM '$GtfsDir\calendar_dates.txt' WITH (FORMAT csv, HEADER true)"

Write-Host "GTFS import complete."
