DROP TABLE IF EXISTS gtfs_stop_times;
DROP TABLE IF EXISTS gtfs_trips;
DROP TABLE IF EXISTS gtfs_routes;
DROP TABLE IF EXISTS gtfs_stops;
DROP TABLE IF EXISTS gtfs_calendar;

CREATE TABLE gtfs_stops (
  stop_lat TEXT,
  wheelchair_boarding TEXT,
  stop_code TEXT,
  stop_lon TEXT,
  stop_timezone TEXT,
  stop_url TEXT,
  parent_station TEXT,
  stop_desc TEXT,
  stop_name TEXT,
  location_type TEXT,
  stop_id TEXT PRIMARY KEY,
  zone_id TEXT
);

CREATE TABLE gtfs_routes (
  route_long_name TEXT,
  route_type TEXT,
  route_text_color TEXT,
  route_color TEXT,
  agency_id TEXT,
  route_id TEXT PRIMARY KEY,
  route_url TEXT,
  route_desc TEXT,
  route_short_name TEXT
);

CREATE TABLE gtfs_trips (
  block_id TEXT,
  route_id TEXT,
  direction_id TEXT,
  trip_headsign TEXT,
  shape_id TEXT,
  service_id TEXT,
  trip_id TEXT PRIMARY KEY
);

CREATE TABLE gtfs_stop_times (
  trip_id TEXT,
  arrival_time TEXT,
  departure_time TEXT,
  stop_id TEXT,
  stop_sequence TEXT,
  stop_headsign TEXT,
  pickup_type TEXT,
  drop_off_type TEXT,
  shape_dist_traveled TEXT,
  timepoint TEXT
);

CREATE TABLE gtfs_calendar (
  service_id TEXT PRIMARY KEY,
  start_date TEXT,
  end_date TEXT,
  monday TEXT,
  tuesday TEXT,
  wednesday TEXT,
  thursday TEXT,
  friday TEXT,
  saturday TEXT,
  sunday TEXT
);

CREATE INDEX IF NOT EXISTS idx_stop_times_trip_stop
  ON gtfs_stop_times(trip_id, stop_id);

CREATE INDEX IF NOT EXISTS idx_trips_route
  ON gtfs_trips(route_id);

CREATE INDEX IF NOT EXISTS idx_trips_service
  ON gtfs_trips(service_id);
