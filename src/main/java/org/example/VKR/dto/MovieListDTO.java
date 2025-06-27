package org.example.VKR.dto;

import org.example.VKR.models.Movie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieListDTO implements Serializable {
private List<MovieDTO> movieDTOList;

    public MovieListDTO(List<MovieDTO> movieDTOList) {
        this.movieDTOList = movieDTOList;
    }

    public List<MovieDTO> getMovieDTOList() {
        return movieDTOList;
    }

    public void setMovieDTOList(List<MovieDTO> movieDTOList) {
        this.movieDTOList = movieDTOList;
    }
}
