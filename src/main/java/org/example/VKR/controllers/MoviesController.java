package org.example.VKR.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.example.VKR.services.MoviesService;
import org.example.VKR.util.MovieNotFoundException;
import org.example.VKR.util.MovieErrorResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final MoviesService moviesService;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    @Autowired
    public MoviesController(MoviesService moviesService, RestTemplate restTemplate, ModelMapper modelMapper) {
        this.moviesService = moviesService;
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

    @GetMapping("/save")
    public ResponseEntity<String> saveMovieInTable(
            @RequestParam(defaultValue = "1") int startPage,
            @RequestParam(defaultValue = "3") int maxPages
    ) {

        int totalSaved = 0;

        for (int page = startPage; page < startPage + maxPages; page++) {
            String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films?page=" + page;

            try {

                JsonNode root = getResponse(url).getBody();
                JsonNode items = root.path("items");

                for (JsonNode item : items) {
                    try {
                        Movie movie = createMovie(item);
                        moviesService.save(movie);
                        System.out.println("Сохранен фильм: " + movie.getFilmName());
                        totalSaved++;
                    } catch (Exception e) {
                        System.err.println("Ошибка произошла на фильма с id: " + item.path("kinopoiskId"));
                    }
                    Thread.sleep(300);

                }
            } catch (Exception e) {
                System.err.println("Ошибка произошла на странице: " + page + e.getMessage());
            }           }
        String message = "Всего добавлено: " + totalSaved;
            System.out.println(message);
            return ResponseEntity.ok(message);
        }

        private boolean checkName (String name){
            return name != null &&
                    !name.isEmpty() &&
                    !name.equalsIgnoreCase("null");
        }

        private String getDescription ( int filmId) throws IOException {
            String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + filmId;

            JsonNode root = getResponse(url).getBody();

            String description = root.path("description").asText();
            String shortDescription = root.path("shortDescription").asText();

            String fullDescription = checkDescription(description) + " " + checkDescription(shortDescription);
            fullDescription = fullDescription.trim();
            return fullDescription.isEmpty() ? "Нет описания" : fullDescription;

        }

        private String checkDescription (String description){
            return (description == null || description.isEmpty() || description.equalsIgnoreCase("null")) ? "" : description;
        }

        @GetMapping("/{filmId}")
        public ResponseEntity<Movie> getMovie ( @PathVariable int filmId){
            Optional<Movie> foundMovie = moviesService.findOne(filmId);
            if (foundMovie.isPresent()) {
                return ResponseEntity.ok(foundMovie.get());
            }

            try {
                String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + filmId;

                JsonNode root = getResponse(url).getBody();

                Movie movie = createMovie(root);

                moviesService.save(movie);
                System.out.println("Сохранен фильм: " + movie.getFilmName());
                return ResponseEntity.ok(movie);
            } catch (MovieNotFoundException | IOException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/all")
        public ResponseEntity<Page<MovieDTO>> getAllMovies (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
            Pageable pageable = PageRequest.of(page, size, Sort.by("rating"));
            Page<Movie> allMovies = moviesService.getAll(pageable);
            Page<MovieDTO> allMoviesDTO = allMovies.map(movie -> convertToMovieDTO(movie));

            return ResponseEntity.ok(allMoviesDTO);
        }

        private Movie createMovie (JsonNode item) throws IOException {
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

        private MovieDTO convertToMovieDTO (Movie movie){
            return modelMapper.map(movie, MovieDTO.class);
        }

        @ExceptionHandler
        private ResponseEntity<MovieErrorResponse> handlerException (MovieNotFoundException e){
            MovieErrorResponse response = new MovieErrorResponse(
                    "Movie with this id wasn't found", System.currentTimeMillis()
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

