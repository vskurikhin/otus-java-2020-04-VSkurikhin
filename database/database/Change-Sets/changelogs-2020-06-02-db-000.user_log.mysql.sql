CREATE TABLE IF NOT EXISTS user_log (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_profile_id   BIGINT NOT NULL,
  date_time         TIMESTAMP NOT NULL,
  FOREIGN KEY (user_info_id) REFERENCES user_profile(id)
) ENGINE = InnoDB;

CREATE INDEX user_log_user_profile_id_7980 USING BTREE ON user_log (user_profile_id);
