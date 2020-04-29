CREATE TABLE user_info (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(256),
  sur_name   VARCHAR(256),
  age        INT,
  sex        CHAR(1),
  city       VARCHAR(256),
  FOREIGN KEY (id) REFERENCES user_profile(id)
) ENGINE = InnoDB;