DELIMITER $$

CREATE PROCEDURE add_movie(
	IN newStar BOOLEAN, IN newGenre BOOLEAN, IN gId INTEGER,
	IN newStarId VARCHAR(10), IN newMovieId VARCHAR(10), IN movieTitle VARCHAR(100), 
	IN releaseYear INTEGER, IN director VARCHAR(100), IN starName VARCHAR(100),
	IN genre VARCHAR(100))
BEGIN
	IF(newStar = TRUE) THEN
		INSERT INTO stars(id, name, birthYear) VALUES(newStarId, starName, NULL);
	END IF;
	
	IF(newGenre = TRUE) THEN 
		INSERT INTO genres(name) VALUES(genre); 
	END IF;
	
	INSERT INTO movies(id, title, year, director) 
		VALUES(newMovieId, movieTitle, releaseYear, director);
	INSERT INTO stars_in_movies(starId, movieId)
		VALUES(newStarId, newMovieId);
	INSERT INTO genres_in_movies(genreId, movieId)
		VALUES(gId, newMovieId);	
END
$$
DELIMITER ;

