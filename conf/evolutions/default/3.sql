# -- Schema creation
# --- !Ups
CREATE TABLE `collectionitem` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user` VARCHAR(128) NOT NULL,
  `releaseId` VARCHAR(128) NOT NULL,
  PRIMARY KEY (`id`)  COMMENT '');

# --- !Downs

DROP TABLE `collectionitem`;
