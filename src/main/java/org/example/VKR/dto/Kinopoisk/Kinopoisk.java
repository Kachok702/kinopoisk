package org.example.VKR.dto.Kinopoisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Kinopoisk {

    @JsonProperty("kinopoiskId")
    private int kinopoiskId;

    @JsonProperty("nameRu")
    private String nameRu;

    @JsonProperty("nameEn")
    private String nameEn;

    @JsonProperty("nameOriginal")
    private String nameOriginal;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("ratingKinopoisk")
    private Double ratingKinopoisk;

    public int getKinopoiskId() {
        return kinopoiskId;
    }

    public void setKinopoiskId(int kinopoiskId) {
        this.kinopoiskId = kinopoiskId;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameOriginal() {
        return nameOriginal;
    }

    public void setNameOriginal(String nameOriginal) {
        this.nameOriginal = nameOriginal;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getRatingKinopoisk() {
        return ratingKinopoisk;
    }

    public void setRatingKinopoisk(Double ratingKinopoisk) {
        this.ratingKinopoisk = ratingKinopoisk;
    }
}
