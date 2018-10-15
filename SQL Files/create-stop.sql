CREATE TABLE IF NOT EXISTS my_stopwords(value VARCHAR(30)) ENGINE = INNODB;
SET GLOBAL innodb_ft_server_stopword_table = 'moviedb/my_stopwords';

ALTER TABLE movies DROP INDEX title;
ALTER TABLE movies ADD FULLTEXT(title);

