CREATE FUNCTION user_profile_id_nextval(label UUID) RETURNS bigint AS $$
DECLARE
  nextval_id BIGINT;
BEGIN
  CASE label
    WHEN '402dd91b-7a14-46ef-adf6-1707135426ee' THEN
      SELECT nextval FROM seq_user_profile_402dd91b_7a14_46ef_adf6_1707135426ee_id_view INTO nextval_id;
    WHEN 'e13cc11b-dee7-4190-8225-3c08caece235' THEN
      SELECT nextval FROM seq_user_profile_e13cc11b_dee7_4190_8225_3c08caece235_id_view INTO nextval_id;
    ELSE
      RAISE EXCEPTION 'Несуществующий label --> %', label;
  END CASE;
  return nextval_id;
END;
$$ LANGUAGE plpgsql;
