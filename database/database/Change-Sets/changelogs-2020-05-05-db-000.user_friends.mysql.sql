CREATE TABLE IF NOT EXISTS user_friends (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_info_id      BIGINT NOT NULL,
  friend_info_id    BIGINT NOT NULL,
  FOREIGN KEY (user_info_id) REFERENCES user_info(id),
  FOREIGN KEY (friend_info_id) REFERENCES user_info(id)
) ENGINE = InnoDB;
