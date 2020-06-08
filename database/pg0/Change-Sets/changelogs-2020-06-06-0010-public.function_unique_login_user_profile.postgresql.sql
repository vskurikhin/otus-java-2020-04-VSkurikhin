CREATE OR REPLACE FUNCTION unique_login_user() RETURNS trigger AS $$
DECLARE
  count BIGINT;
BEGIN
  SELECT COUNT(login) FROM public.user_profile WHERE login = NEW.login INTO count;

  IF count > 0 THEN
    RAISE EXCEPTION 'login exists!';
  END IF;

  return NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_unique_login_user BEFORE INSERT ON user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  FOR EACH ROW EXECUTE PROCEDURE unique_login_user();
