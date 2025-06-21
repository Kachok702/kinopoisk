package org.example.VKR.rerpositories;

import org.example.VKR.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesRepository extends PagingAndSortingRepository<Movie, Integer> {

    boolean existsByFilmId(int filmId);

    Movie findByFilmId(Integer filmId);

    Page<Movie> findAll(Pageable pageable);
}
