CREATE SCHEMA swplanet;

CREATE TABLE swplanet.planet (
  id INT UNIQUE NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  climate VARCHAR(255) NULL,
  terrain VARCHAR(255) NULL,
  film_appearences INT NULL,
  PRIMARY KEY (id));

CREATE TABLE swplanet.user (
  id INT UNIQUE NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  username VARCHAR(255) NULL,
  password VARCHAR(255) NULL,
  authorities VARCHAR(255) NULL,
  PRIMARY KEY (id));

INSERT INTO swplanet.user
(name,
username,
password,
authorities)
VALUES
(
'Luke Skywalker',
'skywalker',
'{bcrypt}$2a$10$L/IbGWTWv376fUN6wT.MVe/iyz/IBo/xnH.MxWW.oY9FuYB.5mwlG',
'ROLE_USER');


INSERT INTO swplanet.user
(name,
username,
password,
authorities)
VALUES
(
'Darth Vader',
'vader',
'{bcrypt}$2a$10$phn.lBNFQ85HEviDhRzhcOpHWokvBlgqeO9PAHcHA9OVa.W6aRhsG',
'ROLE_USER,ROLE_ADMIN');
