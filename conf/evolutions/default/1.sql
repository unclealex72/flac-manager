# -- Schema creation
# --- !Ups
CREATE TABLE "change" (
  "id" INT PRIMARY KEY,
  "relativePath" TEXT NOT NULL,
  "parentRelativePath" TEXT,
  "at" TIMESTAMP NOT NULL,
  "action" VARCHAR(128) NOT NULL,
  "user" VARCHAR(128) NOT NULL);

create sequence "s_change_id";

CREATE TABLE "collectionitem" (
  "id" INT PRIMARY KEY,
  "user" VARCHAR(128) NOT NULL,
  "releaseId" VARCHAR(128) NOT NULL);

create sequence "s_collectionitem_id";

# --- !Downs

DROP TABLE "collectionitem";
DROP TABLE "change";
drop sequence "s_change_id";
drop sequence "s_collectionitem_id";
