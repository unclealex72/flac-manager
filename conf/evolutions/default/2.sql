# -- Schema creation
# --- !Ups

ALTER TABLE "change" ADD COLUMN "parentRelativePath" TEXT;
UPDATE "change"
SET "parentRelativePath" = SUBSTRING("relativePath" FROM 0 FOR
                                     1 + length("relativePath") - length(substring("relativePath" FROM '/[^/]+$')));

# --- !Downs

ALTER TABLE "change" DROP COLUMN "parentRelativePath";
