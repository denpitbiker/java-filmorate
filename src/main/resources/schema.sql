CREATE TABLE IF NOT EXISTS users ( -- "user" is a keyword so it's easier to just name table as a plural
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    birthday DATE NOT NULL CHECK (birthday <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(5) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    mpa_id BIGINT,
    duration_minutes BIGINT NOT NULL CHECK (duration_minutes > 0),
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friend (
    user_id BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);
