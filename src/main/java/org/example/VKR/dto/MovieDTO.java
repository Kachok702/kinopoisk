package org.example.VKR.dto;

import javax.persistence.Column;

import javax.validation.constraints.NotEmpty;

public class MovieDTO {

    @Column(name = "film_id", unique = true)
    private Integer filmId;

    @Column(name = "film_name")
    @NotEmpty
    private String filmName;


    @Column(name = "rating")
    private Double rating;


    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    @Override
    public String toString() {
        return "MovieDTO{" +
                "filmId=" + filmId +
                ", filmName='" + filmName + '\'' +
                ", rating=" + rating +
                '}';
    }
}
