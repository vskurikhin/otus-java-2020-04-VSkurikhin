CREATE TABLE user_info (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(128),
  sur_name   VARCHAR(128),
  age        INT,
  sex        CHAR(1),
  city       VARCHAR(128),
  FOREIGN KEY (id) REFERENCES user_profile(id)
) ENGINE = InnoDB;
