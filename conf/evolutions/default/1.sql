# -- Schema creation
# --- !Ups
create table `CHANGE` (
  relativePath varchar(512) not null,
  parentRelativePath varchar(512),
  at timestamp not null,
  id bigint not null primary key auto_increment,
  action varchar(128) not null,
  user varchar(128) not null
);
create index CHANGE_RELATIVE_PATH on `CHANGE`(relativePath);
create index CHANGE_ACTION on `CHANGE`(action);
create index CHANGE_USER on `CHANGE`(user);

create table COLLECTIONITEM (
  id bigint not null primary key auto_increment,
  releaseId varchar(128) not null,
  artist varchar(512) not null,
  album varchar(512) not null,
  user varchar(128) not null
);

# --- !Downs

DROP TABLE collectionitem;
DROP INDEX CHANGE_RELATIVE_PATH;
DROP TABLE `change`;
