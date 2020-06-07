CREATE TABLE IF NOT EXISTS message_e13cc11b_dee7_4190_8225_3c08caece235
  PARTITION OF message
  FOR VALUES IN ('e13cc11b-dee7-4190-8225-3c08caece235');

ALTER TABLE message_e13cc11b_dee7_4190_8225_3c08caece235
  ALTER COLUMN id
  SET DEFAULT nextval('seq_message_e13cc11b_dee7_4190_8225_3c08caece235_id');

ALTER TABLE message_e13cc11b_dee7_4190_8225_3c08caece235
  ALTER COLUMN id
  SET NOT NULL;

ALTER SEQUENCE seq_message_e13cc11b_dee7_4190_8225_3c08caece235_id
  OWNED BY message_e13cc11b_dee7_4190_8225_3c08caece235.id;

ALTER TABLE message_e13cc11b_dee7_4190_8225_3c08caece235
  ADD CONSTRAINT PK_message_id_label_4210
  PRIMARY KEY (id, label);
