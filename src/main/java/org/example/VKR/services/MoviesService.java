package org.example.VKR.services;

import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.example.VKR.util.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;

    @Autowired
    public MoviesService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    public List<Movie> findAll() {
        return moviesRepository.findAll();
    }



    public Optional<Movie>  findOne(int filmId) {
       return moviesRepository.findByFilmId(filmId);
    }

    @Transactional
    public void save(Movie movie) {
        if (moviesRepository.existsByFilmId(movie.getFilmId())) {
            System.out.println("Фильм уже существует: " + movie.getFilmId());
            return;
        }
        moviesRepository.save(movie);
    }

}
