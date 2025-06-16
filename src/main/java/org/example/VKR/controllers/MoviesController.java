package org.example.VKR.controllers;


import org.example.VKR.dto.MovieDTO;
import org.example.VKR.models.Movie;
import org.example.VKR.services.MoviesService;
import org.example.VKR.util.MovieNotFoundException;
import org.example.VKR.util.MovieErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final MoviesService moviesService;

    @Autowired
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping("/save")
    public ResponseEntity<HttpStatus> saveMovieInTable() {
        moviesService.saveMovieAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("/{filmId}")
    public HttpEntity<Movie> getMovie(@PathVariable int filmId) {
        Movie foundMovie = moviesService.findOne(filmId);
        return ResponseEntity.ok(foundMovie);
    }

    @GetMapping("/newMovies")
    public HttpEntity<List<MovieDTO>> getNewMove(){
        List<MovieDTO> movies = moviesService.getTop10NewMovies();
        return ResponseEntity.ok(movies);
    }

    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handlerException(MovieNotFoundException e) {
        MovieErrorResponse response = new MovieErrorResponse(
                "Movie with this id wasn't found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}

