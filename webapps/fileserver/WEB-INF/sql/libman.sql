CREATE DATABASE weblibman WITH ENCODING 'UTF8';
\c libman_data
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE users(userid uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(), username text NOT NULL UNIQUE, password text NOT NULL);
--CREATE TABLE sessions(sessionid text PRIMARY KEY,userid uuid UNIQUE NOT NULL REFERENCES users(userid));
CREATE TABLE books(bookid uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(), bookname text NOT NULL UNIQUE, stock int NOT NULL CONSTRAINT stock_wholeno CHECK (stock>=0), retdate BIGINT NOT NULL);
CREATE TABLE admin(userid uuid PRIMARY KEY REFERENCES users(uuid));

SELECT EXISTS(SELECT * FROM admin WHERE userid=?);

SELECT bookid,bookname,stock,retdate FROM books ORDER BY bookname OFFSET ? ROWS FETCH FIRST ? ROW ONLY;

CREATE TYPE libroles AS ENUM('libadmin','libuser');
create table roles(username text not null references users(username),rolename libroles not null,primary key(username,rolename));

create table files(username text not null references users(username),filename text not null,foldername text[] not null,parts uuid[] not null,seckeys bytea[] not null,ivs bytea[] not null,primary key(username,filename));