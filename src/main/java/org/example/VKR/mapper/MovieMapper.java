package org.example.VKR.mapper;

import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMapper {

MovieDTO toMovieDTO(Movie movie);

List<MovieDTO> toMovieDTOList(List<Movie> movies);

}
