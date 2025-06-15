package org.example.VKR.rerpositories;

import org.example.VKR.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Integer> {

    boolean existsByFilmId(int filmId);

    Optional<Movie> findByFilmId(Integer filmId);

    Page<Movie> findAll(Pageable pageable);
}
