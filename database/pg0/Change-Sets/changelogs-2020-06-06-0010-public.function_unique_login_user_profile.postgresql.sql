CREATE OR REPLACE FUNCTION unique_login_user() RETURNS trigger AS $$
DECLARE
  login_exists VARCHAR(32);
BEGIN
  SELECT NEW.login FROM public.user_profile WHERE login = NEW.login INTO login_exists;

  IF login_exists IS NULL THEN
    RAISE EXCEPTION 'login exists!';
  END IF;

  return NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_unique_login_user BEFORE INSERT ON user_profile_402dd91b_7a14_46ef_adf6_1707135426ee
  FOR EACH ROW EXECUTE PROCEDURE unique_login_user();
