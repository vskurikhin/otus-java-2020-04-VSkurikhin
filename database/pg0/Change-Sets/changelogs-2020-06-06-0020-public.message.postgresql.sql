CREATE TABLE IF NOT EXISTS message (
  id           BIGINT,
  label        UUID NOT NULL,
  from_id      BIGINT,
  from_label   UUID NOT NULL,
  to_id        BIGINT,
  to_label     UUID NOT NULL,
  text_message VARCHAR(256) NOT NULL)

PARTITION BY LIST (label);
