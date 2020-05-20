CREATE TABLE user_profile (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  login      VARCHAR(32) NOT NULL,
  hash       VARCHAR(64) NOT NULL,
  expired    BOOLEAN DEFAULT false NOT NULL,
  locked     BOOLEAN DEFAULT false NOT NULL,
  UNIQUE     (login)
) ENGINE = InnoDB;
