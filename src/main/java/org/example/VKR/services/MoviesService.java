package org.example.VKR.services;


import com.fasterxml.jackson.databind.JsonNode;

import org.example.VKR.config.RestTemplateService;
import org.example.VKR.dto.MovieDTO;
import org.example.VKR.mapper.MovieMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;

import org.example.VKR.util.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MovieServiceDTO movieServiceDTO;
    private final MoviesRepository moviesRepository;
    private final RestTemplateService restTemplateService;
    private final MovieMapper movieMapper;

    private final String basicURL = "https://kinopoiskapiunofficial.tech/api/v2.2/films";

    @Autowired
    public MoviesService(MovieServiceDTO movieServiceDTO, MoviesRepository moviesRepository, RestTemplateService restTemplateService, MovieMapper movieMapper) {
        this.movieServiceDTO = movieServiceDTO;
        this.moviesRepository = moviesRepository;
        this.restTemplateService = restTemplateService;
        this.movieMapper = movieMapper;
    }


    @Transactional
    public void saveMovieAll(int startPage, int totalPage) {
        int endPage = totalPage == 0 ? startPage + 1 : startPage + totalPage;

                for (int page = startPage; page < endPage; page++) {
            JsonNode root = restTemplateService.getResponse(basicURL + "?page=" + page).getBody();
            JsonNode items = root.path("items");

            List<Movie> movies = new ArrayList<>();

            for (JsonNode item : items) {
                try {
                    movies.add(createMovie(item));
                } catch (Exception e) {
                    System.err.println("Ошибка произошла на фильма с id: " + item.path("kinopoiskId"));
                }
            }
            moviesRepository.saveAll(movies);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    @Transactional
    public boolean saveMovie(Movie movie) {
        if (moviesRepository.existsByFilmId(movie.getFilmId())) {
            System.out.println("Фильм уже существует: " + movie.getFilmId());
            return false;
        }
        moviesRepository.save(movie);
        System.out.println("Сохранен фильм: " + movie.getFilmName());
        return true;
    }

    @Transactional
    public MovieDTO findOne(int filmId) {
        Movie movie = moviesRepository.findByFilmId(filmId);
        if (movie != null) {
            return movieMapper.toMovieDTO(movie);
        }

        try {
            JsonNode root = restTemplateService.getResponse(basicURL + "/" + filmId).getBody();
            Movie newMovie = createMovie(root);
            saveMovie(newMovie);
            return movieMapper.toMovieDTO(newMovie);
        } catch (HttpClientErrorException.NotFound e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден на кинопоиске");
        } catch (IOException e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден в таблице");
        }
    }

    public List<MovieDTO> getSortMovies(String type, String sequence, int limit) {
        List<Movie> movies = movieServiceDTO.getMovies(type, sequence, limit);
        return movieMapper.toMovieDTOList(movies);
    }

    private Movie createMovie(JsonNode item) throws IOException {
        Movie movie = new Movie();
        int filmId = item.path("kinopoiskId").asInt();

        String filmNameRu = item.path("nameRu").asText();
        String filmNameEn = item.path("nameEn").asText();
        String originalName = item.path("nameOriginal").asText();
        int year = item.path("year").asInt();
        double rating = item.path("ratingKinopoisk").asDouble();

        movie.setFilmId(filmId);

        if (checkName(filmNameRu)) {
            movie.setFilmName(filmNameRu);
        } else if (checkName(filmNameEn)) {
            movie.setFilmName(filmNameEn);
        } else {
            movie.setFilmName(originalName);
        }

        movie.setYear(year);
        movie.setRating(rating);

        movie.setDescription(getDescription(filmId));
        return movie;
    }

    private String getDescription(int filmId) {

        JsonNode root = restTemplateService.getResponse(basicURL + "/" + filmId).getBody();

        String description = root.path("description").asText();
        String shortDescription = root.path("shortDescription").asText();

        String fullDescription = checkDescription(description) + " " + checkDescription(shortDescription);
        fullDescription = fullDescription.trim();
        return fullDescription.isEmpty() ? "Нет описания" : fullDescription;

    }

    private String checkDescription(String description) {
        return (description == null || description.isEmpty() || description.equalsIgnoreCase("null")) ? "" : description;
    }

    private boolean checkName(String name) {
        return name != null &&
                !name.isEmpty() &&
                !name.equalsIgnoreCase("null");
    }
}
