package org.example.VKR.controllers;


import org.example.VKR.dto.MovieDTO;
import org.example.VKR.services.MoviesService;
import org.example.VKR.util.MovieNotFoundException;
import org.example.VKR.util.MovieErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    public ResponseEntity<HttpStatus> saveMovieInTable(@RequestParam (value = "startPage", defaultValue = "0")@Min(0) int startPage,
                                                       @RequestParam (value = "totalPage", defaultValue = "0")@Min(0)@Max(5) int totalPage)
                                                        {
        moviesService.saveMovieAll(startPage, totalPage);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{filmId}")
    public HttpEntity<MovieDTO> getMovie(@PathVariable int filmId) {
        MovieDTO foundMovie = moviesService.findOne(filmId);
        return ResponseEntity.ok(foundMovie);
    }

    @GetMapping("/sorted")
    public HttpEntity<List<MovieDTO>> getSortMovieForOneParameters(@RequestParam(value = "type", defaultValue = "rating") String type,
                                                                   @RequestParam(value = "sequence", defaultValue = "desc") String sequence,
                                                                   @RequestParam(value = "limit", defaultValue = "10")@Min(1) @Max(100) int limit) {
        return ResponseEntity.ok(moviesService.getSortMovies(type, sequence, limit));
    }



    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handlerException(MovieNotFoundException e) {
        MovieErrorResponse response = new MovieErrorResponse(
                "Movie with this id wasn't found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}

