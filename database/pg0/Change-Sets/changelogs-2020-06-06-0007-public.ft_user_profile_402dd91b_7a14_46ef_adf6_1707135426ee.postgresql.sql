CREATE FOREIGN TABLE IF NOT EXISTS user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  PARTITION OF user_profile
  FOR VALUES IN ('e13cc11b-dee7-4190-8225-3c08caece235')
  SERVER fdw_postgres_1;
