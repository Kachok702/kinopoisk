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

    @Column(name = "description")
    private String description;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public MovieDTO(Integer filmId, String filmName, Integer year, Double rating, String description) {
        this.filmId = filmId;
        this.filmName = filmName;
        this.year = year;
        this.rating = rating;
        this.description = description;
    }
}
