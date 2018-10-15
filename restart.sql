UPDATE star_helper SET nextId = 0;
UPDATE movie_helper SET nextId = 0;
DELETE from genres_in_movies where movieId NOT LIKE BINARY 'tt%';
DELETE from stars_in_movies where movieId NOT LIKE BINARY 'tt%';
DELETE from stars where id NOT LIKE BINARY 'nm%';
DELETE from movies where id NOT LIKE BINARY 'tt%';
delete from genres where id > 23;
ALTER TABLE genres AUTO_INCREMENT = 23;

select count(*) from genres;
select count(*) from movies;
select count(*) from stars;
select count(*) from genres_in_movies;
select count(*) from stars_in_movies;

SELECT AUTO_INCREMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = "moviedb"
AND TABLE_NAME = "genres"