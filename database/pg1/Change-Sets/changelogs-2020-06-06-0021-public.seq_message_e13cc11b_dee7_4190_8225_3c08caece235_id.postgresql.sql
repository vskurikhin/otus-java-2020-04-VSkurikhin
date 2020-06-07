CREATE SEQUENCE IF NOT EXISTS seq_message_e13cc11b_dee7_4190_8225_3c08caece235_id
  AS BIGINT;

CREATE OR REPLACE VIEW seq_message_e13cc11b_dee7_4190_8225_3c08caece235_id_view
  AS SELECT nextval('seq_message_e13cc11b_dee7_4190_8225_3c08caece235_id')::BIGINT AS nextval;
