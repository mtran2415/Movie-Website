create database moviedb;
use moviedb;

CREATE TABLE movies(
	id			VARCHAR(10) DEFAULT '' NOT NULL,
    title		VARCHAR(100) DEFAULT '' NOT NULL,
    year		INTEGER NOT NULL,
    director	VARCHAR(100) DEFAULT '' NOT NULL,
    PRIMARY KEY (id),
    FULLTEXT(title)
);

CREATE TABLE stars(
	id			VARCHAR(10) DEFAULT '' NOT NULL,
    name 		VARCHAR(100) DEFAULT '' NOT NULL,
    birthYear	INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE stars_in_movies(
	starId	VARCHAR(10) DEFAULT '' NOT NULL,
    movieId	VARCHAR(10) DEFAULT '' NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);


CREATE TABLE genres(
	id	INT NOT NULL AUTO_INCREMENT,
    name 	VARCHAR(32) DEFAULT '' NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE genres_in_movies(
	genreId					INT NOT NULL,
    movieId 					VARCHAR(10) DEFAULT '' NOT NULL,
    FOREIGN KEY(genreId) REFERENCES genres(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards(
	id			VARCHAR(20) DEFAULT '' NOT NULL,
    firstName 	VARCHAR(50) DEFAULT '' NOT NULL,
    lastName 	VARCHAR(50) DEFAULT '' NOT NULL,
    expiration 	date NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE customers(
	id 						INTEGER NOT NULL AUTO_INCREMENT,
    firstName 				VARCHAR(50) DEFAULT '' NOT NULL,
    lastName 				VARCHAR(50) DEFAULT '' NOT NULL,
    ccId 						VARCHAR(20) DEFAULT '' NOT NULL,
    address 					VARCHAR(200) DEFAULT '' NOT NULL,
    email 						VARCHAR(50) DEFAULT '' NOT NULL,
    password 				VARCHAR(20) DEFAULT '' NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales(
	id 				INTEGER NOT NULL AUTO_INCREMENT,
	customerId 		VARCHAR(50) DEFAULT '' NOT NULL,
	movieId 			VARCHAR (50) DEFAULT '' NOT NULL,
	saleDate 			DATE NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE ratings(
	movieId		VARCHAR(10) DEFAULT '' NOT NULL,
    rating 		FLOAT NOT NULL,
    numVotes 	INTEGER NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

