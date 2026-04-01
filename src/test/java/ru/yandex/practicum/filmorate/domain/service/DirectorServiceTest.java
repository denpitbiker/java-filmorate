package ru.yandex.practicum.filmorate.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DirectorServiceTest {

    @Autowired
    private DirectorService directorService;

    @Test
    @DisplayName("Get all directors")
    public void getAllDirectors_getAllDirectors_directorsReturned() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());
        directorService.addDirector(VALID_DIRECTOR_DTO_2.clone());

        Collection<DirectorDto> directors = directorService.getAllDirectors();

        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, directors.size());
    }

    @Test
    @DisplayName("Get existing director by id")
    public void getDirector_getExistingDirectorById_directorReturned() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());

        Assertions.assertNotNull(directorService.getDirector(VALID_DIRECTOR_1.getId()));
        Assertions.assertDoesNotThrow(
                () -> directorService.getDirector(VALID_DIRECTOR_DTO_1.getId()));
    }

    @Test
    @DisplayName("Get non-existing director by id")
    public void getDirector_getNonExistingDirectorById_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> directorService.getDirector(VALID_DIRECTOR_DTO_1.getId())
        );
    }

    @Test
    @DisplayName("Add director")
    public void addDirector_addNewDirector_directorAddedAndReturned() {
        Assertions.assertDoesNotThrow(() -> directorService.addDirector(VALID_DIRECTOR_DTO_1.clone()));
    }

    @Test
    @DisplayName("Add existing director")
    public void addDirector_addExistingDirector_throwDuplicateDataException() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());

        Assertions.assertThrows(
                DuplicatedDataException.class,
                () -> directorService.addDirector(VALID_DIRECTOR_DTO_1.clone()));
    }

    @Test
    @DisplayName("Update director")
    public void updateDirector_updateExistingDirector_directorUpdatedAndReturned() {
        DirectorDto addedDirector = directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());

        addedDirector.setName("Benjamin Button");

        Assertions.assertDoesNotThrow(() -> directorService.updateDirector(addedDirector));
    }

    @Test
    @DisplayName("Update non-existing director")
    public void updateDirector_updateNonExistingDirector_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> directorService.updateDirector(VALID_DIRECTOR_DTO_1.clone())
        );
    }

    @Test
    @DisplayName("Delete existing director")
    public void deleteDirector_deleteExistingDirector_directorDeletedAndReturned() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());

        DirectorDto deletedDirector = directorService.deleteDirector(VALID_DIRECTOR_DTO_1.getId());
        Assertions.assertEquals(VALID_DIRECTOR_DTO_1.getName(), deletedDirector.getName());
    }

    @Test
    @DisplayName("Delete non-existing director")
    public void deleteDirector_deleteNonExistingDirector_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> directorService.deleteDirector(VALID_DIRECTOR_DTO_1.getId())
        );
    }
}
