package org.example.VKR.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "film_id", unique = true, nullable = false)
     private Integer filmId;

    @Column(name = "film_name")
    @NotEmpty
    private String filmName;

    @Column(name = "year")
    private Integer year;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "description")
    private String description;


    public Movie() {
    }

    public Movie(Integer filmId, String filmName, Integer year, Double rating, String description) {
        this.filmId = filmId;
        this.filmName = filmName;
        this.year = year;
        this.rating = rating;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", filmId=" + filmId +
                ", filmName='" + filmName + '\'' +
                ", year=" + year +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                '}';
    }
}
