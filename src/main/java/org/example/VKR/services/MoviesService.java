package org.example.VKR.services;


import org.example.VKR.config.RestTemplateService;
import org.example.VKR.dto.Kinopoisk.Kinopoisk;
import org.example.VKR.dto.Kinopoisk.KinopoiskItem;
import org.example.VKR.dto.Kinopoisk.KinopoiskWithDescription;
import org.example.VKR.dto.MovieDTO;
import org.example.VKR.mapper.MovieMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;

import org.example.VKR.util.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MovieServiceDTO movieServiceDTO;
    private final MoviesRepository moviesRepository;
    private final RestTemplateService restTemplateService;
    private final MovieMapper movieMapper;

    private final String basicURL = "https://kinopoiskapiunofficial.tech/api/v2.2/films";

    @Autowired
    public MoviesService(MovieServiceDTO movieServiceDTO, MoviesRepository moviesRepository, RestTemplateService restTemplateService, MovieMapper movieMapper) {
        this.movieServiceDTO = movieServiceDTO;
        this.moviesRepository = moviesRepository;
        this.restTemplateService = restTemplateService;
        this.movieMapper = movieMapper;
    }

    @Transactional
    public void saveMovieAll(int startPage, int totalPage) {
        int totalMoviesSaved = 0;
        int endPage = totalPage == 0 ? startPage + 1 : startPage + totalPage;

        for (int page = startPage; page < endPage; page++) {
            KinopoiskItem item = restTemplateService.getResponse(basicURL + "?page=" + page, KinopoiskItem.class).getBody();

            if (item.getItems() == null || item.getItems().isEmpty()) {
                System.err.println("Пустой список фильмов на странице: " + page);
                continue;
            }

            List<Movie> movies = new ArrayList<>();
            for (Kinopoisk root : item.getItems()) {
                try {

                    if (moviesRepository.existsByFilmId(root.getKinopoiskId())) {
                        System.out.println("Фильм уже существует: " + root.getKinopoiskId());
                        continue;
                    }
                    KinopoiskWithDescription description = restTemplateService.getResponse(basicURL + "/" + root.getKinopoiskId(), KinopoiskWithDescription.class).getBody();
                    movies.add(createMovie(description));
                    totalMoviesSaved++;
                    System.out.println("Добавлен фильм: " + description.getNameOriginal());
                } catch (Exception e) {
                    System.err.println("Ошибка произошла на фильма с id: " + root.getKinopoiskId());
                }
            }
            moviesRepository.saveAll(movies);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Сохранено: " + totalMoviesSaved + " фильмов");
    }

    @Transactional
    public MovieDTO findOne(int filmId) {
        Movie movie = moviesRepository.findByFilmId(filmId);
        if (movie != null) {
            return movieMapper.toMovieDTO(movie);
        }

        try {
            KinopoiskWithDescription kinopoisk = restTemplateService.getResponse(basicURL + "/" + filmId, KinopoiskWithDescription.class).getBody();

            Movie newMovie = createMovie(kinopoisk);
            saveMovie(newMovie);
            return movieMapper.toMovieDTO(newMovie);
        } catch (HttpClientErrorException.NotFound e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден на кинопоиске");
        } catch (IOException e) {
            throw new MovieNotFoundException("Фильм с id: " + filmId + " не найден в таблице");
        }
    }

    @Transactional
    public void saveMovie(Movie movie) {
        if (moviesRepository.existsByFilmId(movie.getFilmId())) {
            System.out.println("Фильм уже существует: " + movie.getFilmId());
            return;
        }
        moviesRepository.save(movie);
        System.out.println("Сохранен фильм: " + movie.getFilmName());
    }

    public Page<MovieDTO> getSortMovies(String field, String direction, int page, int size) {
        Page<Movie> pageMovie = movieServiceDTO.getFilms(field, direction, page, size);
        Page<MovieDTO> movies = pageMovie.map(movieMapper::toMovieDTO);

        try (PrintWriter printWriter = new PrintWriter("src/main/resources/movie.xml")) {



            for (MovieDTO movieDTO : movies) {
                printWriter.println("filmId: " + movieDTO.getFilmId());
                printWriter.println("filmName: " + movieDTO.getFilmName());
                printWriter.println("year: " + movieDTO.getYear());
                printWriter.println("rating: " + movieDTO.getRating());
                printWriter.println("description: " + movieDTO.getDescription() + "\n");

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }

    private Movie createMovie(KinopoiskWithDescription kinopoisk) throws IOException {
        Movie movie = new Movie();
        movie.setFilmId(kinopoisk.getKinopoiskId());

        String nameRu = kinopoisk.getNameRu();
        String nameEn = kinopoisk.getNameEn();
        String originalName = kinopoisk.getNameOriginal();

        if (checkName(nameRu)) {
            movie.setFilmName(nameRu);
        } else if (checkName(nameEn)) {
            movie.setFilmName(nameEn);
        } else {
            movie.setFilmName(originalName);
        }

        movie.setYear(kinopoisk.getYear());
        movie.setRating(kinopoisk.getRatingKinopoisk());
        movie.setDescription(getDescription(kinopoisk.getDescription(), kinopoisk.getShortDescription()));


        return movie;
    }

    private String getDescription(String description, String shortDescription) {

        String fullDescription = checkDescription(description) + " " + checkDescription(shortDescription);
        fullDescription = fullDescription.trim();
        return fullDescription.isEmpty() ? "Нет описания" : fullDescription;

    }

    private String checkDescription(String description) {
        return (description == null || description.isEmpty() || description.equalsIgnoreCase("null")) ? "" : description;
    }

    private boolean checkName(String name) {
        return name != null &&
                !name.isEmpty() &&
                !name.equalsIgnoreCase("null");
    }
}
