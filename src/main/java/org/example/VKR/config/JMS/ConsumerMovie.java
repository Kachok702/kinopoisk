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
    private final ObjectMapper objectMapper;

    @Autowired
    public ConsumerMovie(MoviesRepository moviesRepository, ObjectMapper objectMapper) {
        this.moviesRepository = moviesRepository;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "movie.queue")
    public void receiveMessage(String jsonMessage) throws JsonProcessingException {

        List<Movie> movieList = objectMapper.readValue(jsonMessage, new TypeReference<>() {});
        List<Integer> filmId = moviesRepository.findAllFilmId();
        List<Movie> result = new ArrayList<>();

        for(Movie movie: movieList){
            if (!filmId.contains(movie.getFilmId())){
                result.add(movie);
            }
        }

       moviesRepository.saveAll(result);
    }
}
