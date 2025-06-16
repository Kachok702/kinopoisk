package org.example.VKR.dto;

import javax.persistence.Column;

import javax.validation.constraints.NotEmpty;

public class MovieDTO {

    @Column(name = "film_id", unique = true, nullable = false)
    private Integer filmId;

    @Column(name = "film_name")
    @NotEmpty
    private String filmName;

    @Column(name = "year")
    private Integer year;

    @Column(name = "rating")
    private Double rating;

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
}
