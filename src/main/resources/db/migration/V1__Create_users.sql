CREATE TABLE movies
(
    id          SERIAL PRIMARY KEY,
    filmId      INTEGER NOT NULL,
    filmName    varchar not null,
    year        integer,
    rating      integer,
    description varchar
);