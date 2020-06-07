CREATE SEQUENCE IF NOT EXISTS seq_message_402dd91b_7a14_46ef_adf6_1707135426ee_id
  AS BIGINT;

CREATE OR REPLACE VIEW seq_message_402dd91b_7a14_46ef_adf6_1707135426ee_id_view
  AS SELECT nextval('seq_user_profile_402dd91b_7a14_46ef_adf6_1707135426ee_id')::BIGINT AS nextval;
