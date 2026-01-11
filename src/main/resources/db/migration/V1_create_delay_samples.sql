CREATE TABLE IF NOT EXISTS delay_samples (
  id BIGSERIAL PRIMARY KEY,
  observed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  route_id TEXT NOT NULL,
  trip_id TEXT NOT NULL,
  stop_id TEXT NOT NULL,
  delay_seconds INT
);

ALTER TABLE delay_samples
ADD CONSTRAINT IF NOT EXISTS ux_delay_samples_trip_stop
UNIQUE (trip_id, stop_id);

CREATE INDEX IF NOT EXISTS idx_delay_samples_observed_at
  ON delay_samples(observed_at);

CREATE INDEX IF NOT EXISTS idx_delay_samples_route_stop_time
  ON delay_samples(route_id, stop_id, observed_at);