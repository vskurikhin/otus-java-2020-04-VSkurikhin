CREATE TABLE IF NOT EXISTS user_profile (
  id      BIGINT,
  login   VARCHAR(32) NOT NULL,
  hash    VARCHAR(64) NOT NULL,
  expired BOOLEAN DEFAULT false NOT NULL,
  locked  BOOLEAN DEFAULT false NOT NULL,
  label   UUID NOT NULL)
PARTITION BY LIST (label);
