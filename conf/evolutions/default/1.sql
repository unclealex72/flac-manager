# -- Schema creation
# --- !Ups
CREATE TABLE "change" (
  "relativePath" TEXT NOT NULL,
  "at"           TIMESTAMP          NOT NULL,
  "id"           BIGINT PRIMARY KEY NOT NULL,
  "action"       VARCHAR(128)       NOT NULL,
  "user"         VARCHAR(128)       NOT NULL
);
CREATE SEQUENCE "s_change_id";

# --- !Downs

DROP SEQUENCE "s_change_id";
DROP TABLE "change";
