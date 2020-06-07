CREATE TABLE IF NOT EXISTS message_402dd91b_7a14_46ef_adf6_1707135426ee
  PARTITION OF message
  FOR VALUES IN ('402dd91b-7a14-46ef-adf6-1707135426ee');

ALTER TABLE message_402dd91b_7a14_46ef_adf6_1707135426ee
  ALTER COLUMN id
  SET DEFAULT nextval('seq_message_402dd91b_7a14_46ef_adf6_1707135426ee_id');

ALTER TABLE message_402dd91b_7a14_46ef_adf6_1707135426ee
  ALTER COLUMN id
  SET NOT NULL;

ALTER SEQUENCE seq_message_402dd91b_7a14_46ef_adf6_1707135426ee_id
  OWNED BY message_402dd91b_7a14_46ef_adf6_1707135426ee.id;

ALTER TABLE message_402dd91b_7a14_46ef_adf6_1707135426ee
  ADD CONSTRAINT PK_message_id_label_4210
  PRIMARY KEY (id, label);

CREATE INDEX IDX_message_from_id_label
  ON message_402dd91b_7a14_46ef_adf6_1707135426ee
  (from_id, from_label);

CREATE INDEX IDX_message_to_id_label
  ON message_402dd91b_7a14_46ef_adf6_1707135426ee
  (to_id, to_label);
