CREATE TABLE IF NOT EXISTS user_profile (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  login      VARCHAR(32) NOT NULL,
  hash       VARCHAR(64) NOT NULL,
  expired    BOOLEAN DEFAULT false NOT NULL,
  locked     BOOLEAN DEFAULT false NOT NULL,
  UNIQUE     (login)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS user_info (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(32),
  sur_name   VARCHAR(32),
  age        INT,
  sex        CHAR(1),
  city       VARCHAR(32),
  FOREIGN KEY (id) REFERENCES user_profile(id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS user_interest (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  user_info_id  INT NOT NULL,
  interest      VARCHAR(64),
  FOREIGN KEY   (user_info_id) REFERENCES user_info(id)
) ENGINE = InnoDB;

INSERT IGNORE INTO user_profile (login, hash, expired, locked)
 VALUES ('klapodav', '$2a$12$T61MWdzkogbAyhidPDO7EO2Lsz.x51frJBn0nlpbRK7/9Qhw3CoiC', 0, 0);

CREATE TABLE IF NOT EXISTS user_friends (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  user_info_id      INT NOT NULL,
  friend_info_id    INT NOT NULL,
  FOREIGN KEY (user_info_id) REFERENCES user_info(id),
  FOREIGN KEY (friend_info_id) REFERENCES user_info(id)
) ENGINE = InnoDB;
