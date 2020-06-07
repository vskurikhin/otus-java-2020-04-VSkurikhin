CREATE TABLE IF NOT EXISTS user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  PARTITION OF user_profile
  FOR VALUES IN ('402dd91b-7a14-46ef-adf6-1707135426ee');

ALTER TABLE user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  ALTER COLUMN id
  SET DEFAULT nextval('seq_user_profile_402dd91b_7a14_46ef_adf6_1707135426ee_id');

ALTER TABLE user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  ALTER COLUMN id
  SET NOT NULL;

ALTER SEQUENCE seq_user_profile_402dd91b_7a14_46ef_adf6_1707135426ee_id
  OWNED BY user_profile_402dd91b_7a14_46ef_adf6_1707135426ee.id;

ALTER TABLE user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  ADD CONSTRAINT PK_id_label_0946
  PRIMARY KEY (id, label);

ALTER TABLE user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  ADD CONSTRAINT UC_login_must_be_different_8515
  UNIQUE (login);
