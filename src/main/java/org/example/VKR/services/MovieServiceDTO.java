package org.example.VKR.services;

import org.example.VKR.models.Movie;

import org.example.VKR.rerpositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovieServiceDTO {

    @PersistenceContext
    private EntityManager entityManager;

    private final MoviesRepository moviesRepository;

    @Autowired
    public MovieServiceDTO(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    public Page<Movie> getFilms(String field, String direction, int page, int size) {


        check(field, direction, size);
Pageable pageable = direction.equalsIgnoreCase("asc")
        ? PageRequest.of(page, size, Sort.by(field).ascending())
        : PageRequest.of(page,size,  Sort.by(field).descending());

        return moviesRepository.findAll(pageable);
    }

    private void check(String field, String direction, int size) {
        List<String> correctField = List.of("filmId", "filmName", "year", "rating");
        if (!correctField.contains(field)) {
            throw new IllegalArgumentException("Incorrect field " + field);
        }

        List<String> correctDirection = List.of("asc", "desc");
        if (!correctDirection.contains(direction)) {
            throw new IllegalArgumentException("Incorrect direction: " + direction);
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Incorrect limit for sort: " + size + " It's should be between 1 and 100");
        }
    }

}
