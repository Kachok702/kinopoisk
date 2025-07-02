package org.example.VKR.config.JMS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class ConsumerMovie {

    private final MoviesRepository moviesRepository;

    @Autowired
    public ConsumerMovie(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    @JmsListener(destination = "movie.queue")
    public void receiveMessage(String jsonMessage) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        List<Movie> movieList = mapper.readValue(jsonMessage, new TypeReference<List<Movie>>() {
        });

        Map<Integer, Movie> uniqueMovies = new HashMap<>();
        for (Movie movie : movieList) {
            uniqueMovies.putIfAbsent(movie.getFilmId(), movie);
        }

        Set<Integer> existingIds = new HashSet<>(moviesRepository.findAllFilmId());


        List<Movie> toSave = uniqueMovies.values().stream()
                .filter(movie -> !existingIds.contains(movie.getFilmId()))
                .collect(Collectors.toList());

        for (Movie movie : toSave) {
            try {
                moviesRepository.save(movie);
            } catch (Exception e) {
                System.err.println("Ошибка произошла на фильме: " + movie.getFilmId() + ". Ошибка: " + e);
            }
        }
    }
}
