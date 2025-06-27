package org.example.VKR.config.JMS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VKR.dto.MovieDTO;
import org.example.VKR.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.List;


@Component
public class ConsumerMovie {

    private final MovieMapper movieMapper;

    @Autowired
    public ConsumerMovie(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
          }

    @JmsListener(destination = "movie.queue")
    public void receiveMessage(String jsonMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<MovieDTO> movies = mapper.readValue(
                jsonMessage,
                new TypeReference<List<MovieDTO>>(){}
        );

        movies.forEach(movieMapper::toMovie);
    }
}
