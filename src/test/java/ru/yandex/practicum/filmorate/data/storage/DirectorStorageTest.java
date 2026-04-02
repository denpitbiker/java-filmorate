package ru.yandex.practicum.filmorate.data.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.data.model.Director;
import ru.yandex.practicum.filmorate.data.storage.api.DirectorStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public abstract class DirectorStorageTest {
    final DirectorStorage repository;

    protected DirectorStorageTest(DirectorStorage repository) {
        this.repository = repository;
    }

    @Test
    @DisplayName("Add correct director")
    public void addDirector_addCorrectDirector_directorAddedAndReturned() {
        Assertions.assertNotNull(
                repository.addDirector(VALID_DIRECTOR_1.clone()),
                "Director should be added and returned"
        );
        Assertions.assertNotNull(
                repository.addDirector(VALID_DIRECTOR_2.clone()),
                "Director should be added and returned"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllDirectors().size());
    }

    @Test
    @DisplayName("Get existing director by id")
    public void getDirector_getExistingDirectorById_directorReturned() {
        Director director = repository.addDirector(VALID_DIRECTOR_1.clone());

        Assertions.assertTrue(repository.getDirector(director.getId()).isPresent());
        Assertions.assertEquals(VALID_DIRECTOR_NAME_1, repository.getDirector(director.getId()).get().getName());
    }

    @Test
    @DisplayName("Delete existing director by id")
    public void deleteDirector_deleteExistingDirectorById_directorDeletedAndReturned() {
        Director director = repository.addDirector(VALID_DIRECTOR_1.clone());

        Director deletedDirector = repository.deleteDirector(director.getId());
        Assertions.assertFalse(repository.getDirector(deletedDirector.getId()).isPresent());
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_ZERO, repository.getAllDirectors().size());
    }

    @Test
    @DisplayName("Update existing director")
    public void updateDirector_updateExistingDirector_directorUpdatedAndReturned() {
        Director addedDirector = repository.addDirector(VALID_DIRECTOR_1.clone());
        addedDirector.setName("Benjamin Button");
        Assertions.assertNotNull(repository.updateDirector(addedDirector), "Director should be updated");
        Assertions.assertTrue(repository.getDirector(addedDirector.getId()).isPresent());
        Assertions.assertEquals(addedDirector.getName(), repository.getDirector(addedDirector.getId()).get().getName());
    }

    @Test
    @DisplayName("Get all directors")
    public void getAllDirectors_getAllExistingDirectors_directorsReturned() {
        repository.addDirector(VALID_DIRECTOR_1.clone());
        repository.addDirector(VALID_DIRECTOR_2.clone());

        List<Director> directors = repository.getAllDirectors();

        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, directors.size());
        Assertions.assertEquals(VALID_DIRECTOR_1.getName(), directors.getFirst().getName());
        Assertions.assertEquals(VALID_DIRECTOR_2.getName(), directors.getLast().getName());
    }

    @Test
    @DisplayName("Check existing director id")
    public void hasDirectorId_checkExistingDirectorIdInStorage_returnTrue() {
        repository.addDirector(VALID_DIRECTOR_1.clone());

        Assertions.assertTrue(repository.hasDirectorId(VALID_DIRECTOR_1.getId()));
    }

    @Test
    @DisplayName("Check non-existing director id")
    public void hasDirectorId_checkNonExistingDirectorIdInStorage_returnFalse() {
        repository.addDirector(VALID_DIRECTOR_1.clone());

        Assertions.assertFalse(repository.hasDirectorId(VALID_DIRECTOR_2.getId()));
    }
}
