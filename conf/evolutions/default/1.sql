# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat_message (
  id                        serial not null,
  message                   varchar(255),
  sender_id                 integer,
  constraint pk_chat_message primary key (id))
;

create table chat_user (
  id                        serial not null,
  identification            varchar(255),
  name                      varchar(255),
  constraint pk_chat_user primary key (id))
;

create table test_user (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_test_user primary key (email))
;

alter table chat_message add constraint fk_chat_message_sender_1 foreign key (sender_id) references chat_user (id);
create index ix_chat_message_sender_1 on chat_message (sender_id);



# --- !Downs

drop table if exists chat_message cascade;

drop table if exists chat_user cascade;

drop table if exists test_user cascade;

