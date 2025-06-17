package org.example.VKR.mapper;

import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MovieMapperImpl implements MovieMapper {
    @Override
    public MovieDTO toMovieDTO(Movie movie) {
        if (movie == null) {
            return null;
        }

        Integer filmId = movie.getFilmId();
        String filmName = movie.getFilmName();
        Integer year = movie.getYear();
        Double rating = movie.getRating();
        String description = movie.getDescription();

        return new MovieDTO(filmId, filmName, year, rating, description);
    }

    @Override
    public List<MovieDTO> toMovieDTOList(List<Movie> movies) {
        if (movies.isEmpty()) {
            return null;
        }

        List<MovieDTO> movieDTOList = new ArrayList<>(movies.size());
        for (Movie movie : movies) {
            movieDTOList.add(toMovieDTO(movie));
        }
        return movieDTOList;
    }
}
