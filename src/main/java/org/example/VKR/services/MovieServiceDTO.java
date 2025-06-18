package org.example.VKR.services;

import org.example.VKR.models.Movie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovieServiceDTO {

    @PersistenceContext
    private EntityManager entityManager;


    public List<Movie> getMovies(String parameter, String sequence, int limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = builder.createQuery(Movie.class);

        Root<Movie> root = criteriaQuery.from(Movie.class);

        checkParameter(parameter);
        checkLimit(limit);
        Order order = sequence.equalsIgnoreCase("asc") ? builder.asc(root.get(parameter))
                : builder.desc(root.get(parameter));

        criteriaQuery.orderBy(order);

        TypedQuery<Movie> query = entityManager.createQuery(criteriaQuery).setMaxResults(limit);

        return query.getResultList();
    }

    private void checkParameter(String sequence) {
        List<String> correctParameter = List.of("filmId", "filmName", "year", "rating");
        if (!correctParameter.contains(sequence)) {
            throw new IllegalArgumentException("Incorrect type for sort " + sequence);
        }
    }

    private void checkLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Incorrect limit for sort: " + limit + " It's should be between 1 and 100");
        }
    }

}
