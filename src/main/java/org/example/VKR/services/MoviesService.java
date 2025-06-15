package org.example.VKR.services;


import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;

    @Autowired
    public MoviesService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

      public Optional<Movie> findOne(int filmId) {
       return moviesRepository.findByFilmId(filmId);
    }

    public Page<Movie> getAll(Pageable pageable){
        return moviesRepository.findAll(pageable);
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
