CREATE TABLE user_interest (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_info_id BIGINT,
  interest     VARCHAR(256),
  FOREIGN KEY (user_info_id) REFERENCES user_info(id)
) ENGINE = InnoDB;