package org.example.VKR.services;

import org.example.VKR.models.Movie;

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

    public Page<Movie> getFilms(String field, String direction, int page, int size) {

        check(field, direction, size);

        Pageable pageable = direction.equalsIgnoreCase("asc")
                ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(field)))
                : PageRequest.of(page, size, Sort.by(Sort.Order.desc(field)));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = builder.createQuery(Movie.class);
        Root<Movie> root = criteriaQuery.from(Movie.class);

        Order order = direction.equalsIgnoreCase("asc")
                ? builder.asc(root.get(field))
                : builder.desc(root.get(field));
        criteriaQuery.orderBy(order);

        TypedQuery<Movie> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Movie> movies = query.getResultList();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Movie> countRoot = countQuery.from(Movie.class);
        countQuery.select(builder.count(countRoot));

        Long total = entityManager.createQuery(countQuery).getSingleResult();


        return new PageImpl<>(movies, pageable, total);
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
