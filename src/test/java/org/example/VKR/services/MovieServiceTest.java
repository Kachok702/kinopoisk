package org.example.VKR.services;

import org.example.VKR.config.Rest.RestTemplateService;
import org.example.VKR.dto.Kinopoisk.KinopoiskWithDescription;
import org.example.VKR.dto.MovieDTO;
import org.example.VKR.mapper.MovieMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.example.VKR.util.MovieNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MoviesRepository moviesRepository;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MoviesService moviesService;

    @Test
    void testFindOneWhenMovieIsPresent() {
        int filmId = 1;
        Movie movie = new Movie(filmId, "Test Name", 2025, 10.0, "Test movie");
        MovieDTO movieDTO = new MovieDTO(filmId, "Test Name", 2025, 10.0, "Test movie");

        when(moviesRepository.findByFilmId(filmId)).thenReturn(movie);
        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        MovieDTO findWithFilmId = moviesService.findOne(filmId);

        assertEquals(1, findWithFilmId.getFilmId());
        assertEquals("Test Name", findWithFilmId.getFilmName());
        assertEquals(10.0, findWithFilmId.getRating());
        assertEquals(2025, findWithFilmId.getYear());
        assertEquals("Test movie", findWithFilmId.getDescription());


        verify(moviesRepository, times(1)).findByFilmId(filmId);
        verify(movieMapper, times(1)).toMovieDTO(movie);
    }

    @Test
    void testMovieToMovieDTO() {
        int filmId = 1;
        int Id = 10;
        Movie movie = new Movie(filmId, "Test Name", 2025, 10.0, "Test movie");
        movie.setId(Id);

        MovieDTO movieDTO = new MovieDTO(1, "Test Name", 2025, 10.0, "Test movie");

        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        MovieDTO movieDTOFromMovie = movieMapper.toMovieDTO(movie);

        assertEquals(1, movieDTOFromMovie.getFilmId());
        assertEquals("Test Name", movieDTOFromMovie.getFilmName());
        assertEquals(10.0, movieDTOFromMovie.getRating());
        assertEquals(2025, movieDTOFromMovie.getYear());
        assertEquals("Test movie", movieDTOFromMovie.getDescription());


        verify(movieMapper, times(1)).toMovieDTO(movie);
    }

    @Test
    void testRestTemplateNewMovie() {
        int filmId = 10;

        when(moviesRepository.findByFilmId(filmId)).thenReturn(null);

        KinopoiskWithDescription movieFromRest = new KinopoiskWithDescription();
        movieFromRest.setKinopoiskId(filmId);
        movieFromRest.setYear(2020);
        movieFromRest.setRatingKinopoisk(9.0);
        movieFromRest.setNameOriginal("Original Name");
        movieFromRest.setNameRu("Name Ru");
        movieFromRest.setNameEn("Name En");
        movieFromRest.setShortDescription("Short Description");
        movieFromRest.setDescription("Description");

        when(restTemplateService.getResponse(anyString(), eq(KinopoiskWithDescription.class))).thenReturn(ResponseEntity.ok(movieFromRest));

        MovieDTO movieDTO = new MovieDTO(filmId, "Name Ru", 2020, 9.0, "Description Short Description");

        when(movieMapper.toMovieDTO(any(Movie.class))).thenReturn(movieDTO);

        MovieDTO result = moviesService.findOne(filmId);

        assertEquals(10, result.getFilmId());
        assertEquals("Name Ru", result.getFilmName());
        assertEquals(9.0, result.getRating());
        assertEquals(2020, result.getYear());
        assertEquals("Description Short Description", result.getDescription());

        verify(moviesRepository, times(1)).findByFilmId(filmId);
        verify(restTemplateService, times(1)).getResponse(anyString(), eq(KinopoiskWithDescription.class));
        verify(moviesRepository, times(1)).save(any());
        verify(movieMapper, times(1)).toMovieDTO(any());

    }

    @Test
    void whenException() throws HttpClientErrorException {
        int filmId = 10;
        String message = "Фильм с id: " + filmId + " не найден на кинопоиске";

        when(moviesRepository.findByFilmId(filmId)).thenReturn(null);
        when(restTemplateService.getResponse(anyString(), eq(KinopoiskWithDescription.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, message));

        Exception exception = assertThrows(MovieNotFoundException.class, () ->
                moviesService.findOne(filmId));

        assertEquals(message, exception.getMessage());

        verify(moviesRepository, times(1)).findByFilmId(filmId);
        verify(restTemplateService, times(1)).getResponse(anyString(), eq(KinopoiskWithDescription.class));
        verify(moviesRepository, never()).save(any());
        verify(movieMapper, never()).toMovieDTO(any());
    }
}
