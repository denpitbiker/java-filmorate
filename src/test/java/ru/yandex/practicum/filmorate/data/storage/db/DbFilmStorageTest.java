package ru.yandex.practicum.filmorate.data.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.data.storage.FilmStorageTest;
import ru.yandex.practicum.filmorate.data.storage.impl.db.FilmDbStorage;

@JdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DbFilmStorageTest extends FilmStorageTest {

    protected DbFilmStorageTest(@Autowired JdbcTemplate jdbcTemplate) {
        super(new FilmDbStorage(jdbcTemplate));
    }
}
