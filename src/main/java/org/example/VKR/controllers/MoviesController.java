package org.example.VKR.controllers;

import org.example.VKR.services.MoviesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MoviesController {

    private final MoviesService moviesService;


    @Autowired
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
      }
}
