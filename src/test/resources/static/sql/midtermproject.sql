drop schema if exists midtermproject;
create schema midtermproject;
use midtermproject;

create table user (
	id bigint auto_increment not null,
    name varchar(255),
    username varchar(255),
    password varchar(255),
    primary key (id)
);

create table role (
  id bigint auto_increment not null,
  name varchar(255),
  user_id bigint,
  primary key (id),
  foreign key (user_id) REFERENCES user(id)
);

create table third_party (
	id bigint auto_increment not null,
	name varchar(255),
    hashkey varchar(255),
    primary key (id)
);

