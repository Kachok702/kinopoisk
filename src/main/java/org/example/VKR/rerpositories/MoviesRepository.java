package org.example.VKR.rerpositories;

import org.example.VKR.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Integer> {

    boolean existsByFilmId(int filmId);

    Movie findByFilmId(Integer filmId);

    Page<Movie> findAll(Pageable pageable);

    @Query("SELECT filmId from Movie")
    Set<Integer> findAllFilmId();
}
