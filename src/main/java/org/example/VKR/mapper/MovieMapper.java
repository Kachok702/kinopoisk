package org.example.VKR.mapper;

import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMapper {

//    @Mapping(target = "filmId", source = "movie.filmId")
//    @Mapping(target = "filmName", source = "movie.filmName")
//    @Mapping(target = "year", source = "movie.year")
//    @Mapping(target = "rating", source = "movie.rating")
//    @Mapping(target = "description", source = "movie.description")
MovieDTO toMovieDTO(Movie movie);


//    @Mapping(target = "filmId", source = "movieDTO.filmId")
//    @Mapping(target = "filmName", source = "movieDTO.filmName")
//    @Mapping(target = "year", source = "movieDTO.year")
//    @Mapping(target = "rating", source = "movieDTO.rating")
//    @Mapping(target = "description", source = "movieDTO.description")
Movie toMovie(MovieDTO movieDTO);

}
