# -- Schema creation
# --- !Ups
create table `change` (
  relativePath varchar(256) not null,
  parentRelativePath varchar(256),
  at timestamp not null,
  id bigint not null primary key auto_increment,
  action varchar(128) not null,
  user varchar(128) not null
);
create table collectionitem (
  id bigint not null primary key auto_increment,
  releaseId varchar(128) not null,
  user varchar(128) not null
);

# --- !Downs

DROP TABLE collectionitem;
DROP TABLE `change`;
