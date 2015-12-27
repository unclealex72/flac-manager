# -- Schema creation
# --- !Ups
CREATE TABLE `change` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `relativePath` TEXT NOT NULL,
  `parentRelativePath` TEXT NOT NULL,
  `at` DATETIME NOT NULL,
  `action` VARCHAR(128) NOT NULL,
  `user` VARCHAR(128) NOT NULL,
  PRIMARY KEY (`id`)  COMMENT '');

# --- !Downs

DROP TABLE `change`;
