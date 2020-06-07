CREATE TABLE IF NOT EXISTS user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  PARTITION OF user_profile
  FOR VALUES IN ('e13cc11b-dee7-4190-8225-3c08caece235');

ALTER TABLE user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  ALTER COLUMN id
  SET DEFAULT nextval('seq_user_profile_e13cc11b_dee7_4190_8225_3c08caece235_id');

ALTER TABLE user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  ALTER COLUMN id
  SET NOT NULL;

ALTER SEQUENCE seq_user_profile_e13cc11b_dee7_4190_8225_3c08caece235_id
  OWNED BY user_profile_e13cc11b_dee7_4190_8225_3c08caece235.id;

ALTER TABLE user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  ADD CONSTRAINT PK_id_label_0946
  PRIMARY KEY (id, label);

ALTER TABLE user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  ADD CONSTRAINT UC_login_must_be_different_8515
  UNIQUE (login);
