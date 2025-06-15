CREATE TABLE movies
(
    id          SERIAL PRIMARY KEY,
    film_id      INTEGER,
    film_name    varchar not null ,
    year        integer,
    rating      double precision,
    description varchar
);