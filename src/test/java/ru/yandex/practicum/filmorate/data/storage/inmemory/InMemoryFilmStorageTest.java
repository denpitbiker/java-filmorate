package ru.yandex.practicum.filmorate.data.storage.inmemory;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.data.annotation.InMemoryStorage;
import ru.yandex.practicum.filmorate.data.storage.FilmStorageTest;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InMemoryFilmStorageTest extends FilmStorageTest {

    protected InMemoryFilmStorageTest(@Autowired @InMemoryStorage FilmStorage repository) {
        super(repository);
    }
}
