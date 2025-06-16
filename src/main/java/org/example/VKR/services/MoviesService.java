package org.example.VKR.services;


import com.fasterxml.jackson.databind.JsonNode;

import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;

import org.example.VKR.util.MovieNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    private final String basicURL = "https://kinopoiskapiunofficial.tech/api/v2.2/films";

    @Autowired
    public MoviesService(MoviesRepository moviesRepository, RestTemplate restTemplate, ModelMapper modelMapper) {
        this.moviesRepository = moviesRepository;
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
    }

    private ResponseEntity<JsonNode> getResponse(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "14088d89-fa7f-4114-acc2-8a1fe9f0baa2");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );
        return response;
    }

    @Transactional
    public void saveMovieAll() {
        JsonNode root = getResponse(basicURL).getBody();
        JsonNode items = root.path("items");

        List<Movie> movies = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                movies.add(createMovie(item));
                Thread.sleep(300);
            } catch (Exception e) {
                System.err.println("Ошибка произошла на фильма с id: " + item.path("kinopoiskId"));
            }
        }
        moviesRepository.saveAll(movies);
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
    public Movie findOne(int filmId) {
        Movie movie = moviesRepository.findByFilmId(filmId);
        if (movie != null) {
            return movie;
        }

        try {
            JsonNode root = getResponse(basicURL + "/" + filmId).getBody();
            Movie newMovie = createMovie(root);
            saveMovie(newMovie);
            return newMovie;
        } catch (HttpClientErrorException.NotFound e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден на кинопоиске");
        } catch (IOException e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден в таблице");
        }
    }

    public List<MovieDTO> getTop10NewMovies() {
        Sort sort = Sort.by(Sort.Direction.DESC, "year");
        Pageable topTen = PageRequest.of(0, 10, sort);

        List<Movie> movies = moviesRepository.findAll(topTen).getContent();

        return movies.stream().map(this::convertToMovieDTO).collect(Collectors.toList());
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

        JsonNode root = getResponse(basicURL + "/" + filmId).getBody();

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

    private MovieDTO convertToMovieDTO(Movie movie) {
        return modelMapper.map(movie, MovieDTO.class);
    }
}
