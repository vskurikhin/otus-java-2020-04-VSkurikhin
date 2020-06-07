CREATE OR REPLACE FUNCTION unique_login_user() RETURNS trigger AS $$
DECLARE
  count BIGINT;
BEGIN
  SELECT COUNT(id, label) FROM public.user_profile WHERE login = NEW.login INTO count;

  IF count > 0 THEN
    RAISE EXCEPTION 'login exists!';
  END IF;

  return NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_unique_login_user BEFORE INSERT ON user_profile_e13cc11b_dee7_4190_8225_3c08caece235
  FOR EACH ROW EXECUTE PROCEDURE unique_login_user();
