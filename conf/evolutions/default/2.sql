# -- Schema creation
# --- !Ups

ALTER TABLE `change` MODIFY `parentRelativePath` TEXT;

# --- !Downs

DELETE FROM `change` WHERE `parentRelativePath` IS NULL;
ALTER TABLE `change` MODIFY `parentRelativePath` TEXT NOT NULL;
