package ru.yandex.practicum.filmorate.data.storage.inmemory;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.data.annotation.InMemoryStorage;
import ru.yandex.practicum.filmorate.data.storage.UserStorageTest;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InMemoryUserStorageTest extends UserStorageTest {

    protected InMemoryUserStorageTest(@Autowired @InMemoryStorage UserStorage repository) {
        super(repository);
    }
}
