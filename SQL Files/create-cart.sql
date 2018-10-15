CREATE TABLE shopping_cart(
	customerId 	INTEGER NOT NULL,
	movieId		VARCHAR (50) DEFAULT '' NOT NULL,
	quantity	INTEGER NOT NULL,
	CONSTRAINT customerOrder UNIQUE(customerId, movieId),
	FOREIGN KEY (customerId) REFERENCES customers(id),
	FOREIGN KEY (movieId) REFERENCES movies(id)
);
