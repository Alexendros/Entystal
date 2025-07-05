-- Activos
CREATE TABLE IF NOT EXISTS asset (
  id TEXT PRIMARY KEY,
  description TEXT NOT NULL,
  timestamp BIGINT NOT NULL,
  data_points INTEGER,
  lines_of_code INTEGER,
  score DOUBLE PRECISION
);

-- Pasivos
CREATE TABLE IF NOT EXISTS liability (
  id TEXT PRIMARY KEY,
  description TEXT NOT NULL,
  timestamp BIGINT NOT NULL,
  severity INTEGER,
  cost_estimate NUMERIC,
  compliance_issue TEXT
);

-- Inversiones
CREATE TABLE IF NOT EXISTS investment (
  id TEXT PRIMARY KEY,
  description TEXT NOT NULL,
  timestamp BIGINT NOT NULL,
  amount NUMERIC,
  participants INTEGER,
  hours INTEGER
);

-- Seguridad a nivel de fila
ALTER TABLE asset ENABLE ROW LEVEL SECURITY;
CREATE POLICY all_assets ON asset FOR ALL USING (true) WITH CHECK (true);

ALTER TABLE liability ENABLE ROW LEVEL SECURITY;
CREATE POLICY all_liabilities ON liability FOR ALL USING (true) WITH CHECK (true);

ALTER TABLE investment ENABLE ROW LEVEL SECURITY;
CREATE POLICY all_investments ON investment FOR ALL USING (true) WITH CHECK (true);
