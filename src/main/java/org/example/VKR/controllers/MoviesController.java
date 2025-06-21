package org.example.VKR.controllers;


import org.example.VKR.dto.MovieDTO;
import org.example.VKR.services.MoviesService;
import org.example.VKR.util.MovieNotFoundException;
import org.example.VKR.util.MovieErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final MoviesService moviesService;

    @Autowired
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping("/save")
    public ResponseEntity<HttpStatus> saveMovieInTable(@RequestParam(value = "startPage", defaultValue = "1") @Min(1) int startPage,
                                                       @RequestParam(value = "totalPage", defaultValue = "0") @Min(0) @Max(5) int totalPage) {
        moviesService.saveMovieAll(startPage, totalPage);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{filmId}")
    public HttpEntity<MovieDTO> getMovie(@PathVariable int filmId) {
        MovieDTO foundMovie = moviesService.findOne(filmId);
        return ResponseEntity.ok(foundMovie);
    }

    @GetMapping("/sorted")
    public HttpEntity<Page<MovieDTO>> getSortMovie(@RequestParam(value = "field", defaultValue = "rating") String field,
                                                   @RequestParam(value = "direction", defaultValue = "desc") String direction,
                                                   @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                                                   @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(moviesService.getSortMovies(field, direction, page, size));
    }


    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handlerException(MovieNotFoundException e) {
        MovieErrorResponse response = new MovieErrorResponse(
                "Movie with this id wasn't found. Problem is: " + e, System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}

