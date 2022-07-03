drop schema if exists midtermproject;
create schema midtermproject;
use midtermproject;

DELETE FROM operation WHERE id>=1;
DELETE FROM checking_acc WHERE id>=1;
DELETE FROM student_checking_acc WHERE id>=1;
DELETE FROM savings_acc WHERE id>=1;
DELETE FROM credit_card_acc WHERE id>=1;
DELETE FROM account WHERE id>=1;
DELETE FROM account_holder WHERE id>=1;
DELETE FROM third_party WHERE id>=1;
DELETE FROM admin WHERE id>=1;
DELETE FROM role WHERE id>=1;
DELETE FROM user WHERE id>=1;

SELECT * FROM operation WHERE (origin_account =11 OR destination_account=11) AND Date(transference_date) > NOW() - INTERVAL 1 SECOND;

SELECT MAX(t.sum) FROM (SELECT DATE(transference_date) AS transaction_date, SUM(amount) AS sum FROM operation WHERE origin_account = 11 GROUP BY (transaction_date)) AS t;

SELECT SUM(amount) AS sum FROM operation WHERE origin_account = 9 AND transference_date >= NOW() - INTERVAL 1 DAY;

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
    primary key (id),

);
