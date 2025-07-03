package org.example.VKR.config.JMS;

import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;


@Component
public class ConsumerMovie {

    private final MoviesRepository moviesRepository;

    @Autowired
    public ConsumerMovie(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    @JmsListener(destination = "movie.queue")
    public void receiveMessage(List<Movie> movieList){

        List<Integer> filmIdList = moviesRepository.findAllFilmId();

        List<Movie> result = new ArrayList<>();

        for (Movie movie : movieList) {
            if (!filmIdList.contains(movie.getFilmId())) {
                result.add(movie);
            }
        }
        moviesRepository.saveAll(result);
    }
}
