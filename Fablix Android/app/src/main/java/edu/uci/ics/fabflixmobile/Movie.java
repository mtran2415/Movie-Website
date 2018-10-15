package edu.uci.ics.fabflixmobile;

public class Movie {
    private String title, year, director, genres, stars, id;

    public Movie(String title, String year, String director, String genres, String stars, String id) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }

    public String getId() {
        return id;
    }

}
