CREATE TABLE user_interest (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_info_id BIGINT NOT NULL,
  interest     VARCHAR(64),
  FOREIGN KEY (user_info_id) REFERENCES user_info(id)
) ENGINE = InnoDB;
