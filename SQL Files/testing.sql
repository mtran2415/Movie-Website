use moviedb;

SELECT m.title, m.year, m.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars
FROM movies as m, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim, ratings as r
WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.Id and m.id = r.movieId
GROUP BY m.title, m.year, m.director, r.rating
ORDER BY r.rating desc
LIMIT 20;

SELECT * from movies where director like '%Nick Clark';

SELECT m.title, m.year, m.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars
FROM movies as m, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim, ratings as r
WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.Id and m.id = r.movieId and s.name like '%Nick%'
GROUP BY m.title, m.year, m.director, r.rating;

SELECT m.title, m.year, m.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars
FROM movies as m, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim, ratings as r
WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.Id and m.id = r.movieId and m.director like '%Louis%'
GROUP BY m.title, m.year, m.director, r.rating;

SELECT m.title, m.year, m.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars
FROM movies as m, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim, ratings as r
WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.Id and m.id = r.movieId and s.name like '%Tom%'
GROUP BY m.title, m.year, m.director, r.rating;

SELECT sub.id, sub.title, sub.year, sub.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars, group_concat(DISTINCT s.id ORDER BY s.name SEPARATOR ' ') AS starIds 
            		FROM (SELECT m.id, m.title, m.year, m.director   
            		FROM movies as m, stars as s, stars_in_movies as sim
            		WHERE m.id = sim.movieId and sim.starId = s.id and m.title like '%Henry%') as sub LEFT JOIN ratings as r ON sub.id = r.movieId, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim
                    WHERE sub.id = sim.movieId and sim.starId = s.id and sub.id = gim.movieId and gim.genreId = g.Id
            		GROUP BY sub.id, sub.title, sub.year, sub.director, r.rating 
            		ORDER BY sub.title asc
					LIMIT 10 OFFSET 0;
                    
SELECT sub.id, sub.title, sub.year, sub.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars, group_concat(DISTINCT s.id ORDER BY s.name SEPARATOR ' ') AS starIds 
					FROM (SELECT m.id, m.title, m.year, m.director 
                    FROM movies as m, stars as s, stars_in_movies as sim 
                    WHERE m.id = sim.movieId and sim.starId = s.id and m.title like '%Henry%') as sub LEFT JOIN ratings as r ON sub.id = r.movieId, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim 
                    WHERE sub.id = sim.movieId and sim.starId = s.id and sub.id = gim.movieId and gim.genreId = g.Id
                    GROUP BY sub.id, sub.title, sub.year, sub.director, r.rating ORDER BY sub.title asc LIMIT 10 OFFSET 0;


select * FROM movies LEFT JOIN ratings as r ON movies.id = r.movieId where movies.id = 'tt0363261'
                    
