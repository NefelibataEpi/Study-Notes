package main.java.model;

public class Movie {
    // state
    private String id;
    private String title;
    private String genre;
    private String year;
    private double rating;

    // constructor
    public Movie() {}
    public Movie(String id, String title, String genre, String year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    // setters
    public void setId(String id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getGenre() {
        return genre;
    }
    public String getYear() {
        return year;
    }
    public double getRating() {
        return rating;
    }

    // String
    @Override
    public String toString() {
        return "Movie{id = " + this.id + ", title = " + this.title +
                ", genre = " + this.genre + ", year = " + this.year + ", rating = " + this.rating + "}";
    }
}
