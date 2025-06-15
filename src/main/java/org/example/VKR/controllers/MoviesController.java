package org.example.VKR.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.services.MoviesService;
import org.example.VKR.util.MovieErrorResponse;

import org.example.VKR.util.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final MoviesService moviesService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MoviesController(MoviesService moviesService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.moviesService = moviesService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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

    @GetMapping
    public ResponseEntity<HttpStatus> saveMovieInTable() throws IOException {

        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";

        JsonNode root = getResponse(url).getBody();
        JsonNode items = root.path("items");

        for (JsonNode item : items) {
            try {
                int filmId = item.path("kinopoiskId").asInt();

                if(moviesService.movieDuplicate(filmId)){
                    System.out.println("Фильм уже существует");
                    continue;
                }

                String filmNameRu = item.path("nameRu").asText();
                String filmNameEn = item.path("nameEn").asText();
                String originalName = item.path("nameOriginal").asText();
                int year = item.path("year").asInt();
                double rating = item.path("ratingKinopoisk").asDouble();


                Movie movie = new Movie();
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
                moviesService.save(movie);
                System.out.println("Сохранен фильм: " + movie.getFilmName());

            Thread.sleep(200);
            } catch (Exception e) {
                System.err.println("Ошибка произошла на фильма с id: " + item.path("kinopoiskId"));
            }
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private boolean checkName(String name) {
        return name != null &&
                !name.isEmpty() &&
                !name.equalsIgnoreCase("null");
    }

    private String getDescription(int filmId) throws IOException {
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + filmId;

        JsonNode root = getResponse(url).getBody();

        String description = root.path("description").asText();
        String shortDescription = root.path("shortDescription").asText();

        String fullDescription = checkDescription(description) + " " + checkDescription(shortDescription);
        fullDescription = fullDescription.trim();
        return fullDescription.isEmpty() ? "Нет описания" : fullDescription;

    }

    private String checkDescription(String description) {
        return (description == null || description.isEmpty() || description.equalsIgnoreCase("null")) ? "" : description;
    }

    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handlerException(MovieNotFoundException e) {
        MovieErrorResponse response = new MovieErrorResponse(
                "Movie with this id wasn't found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}

