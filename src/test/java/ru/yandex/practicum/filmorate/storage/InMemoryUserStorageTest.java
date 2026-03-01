package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InMemoryUserStorageTest {

    @Autowired
    private InMemoryUserStorage repository;

    @Test
    @DisplayName("Add correct user")
    public void addUser_addCorrectUser_userAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> repository.addUser(VALID_USER_1.clone()),
                "User should be added without exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> repository.addUser(VALID_USER_2.clone()),
                "Second user should be added without exceptions"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllUsers().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Get existing user by id")
    public void getUser_getUserByExistingId_userReturnedNoExceptions() {
        User user = repository.addUser(VALID_USER_1.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.getUser(user.getId()),
                "User should be returned for existing id"
        );
    }

    @Test
    @DisplayName("Get non-existing user by id")
    public void getUser_getUserByNonExistingId_NotFoundException() {
        repository.addUser(VALID_USER_1.clone());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> repository.getUser(NON_EXISTING_ID),
                "Should not return user for non-existing id"
        );
    }

    @Test
    @DisplayName("Remove existing user by id")
    public void removeUser_removeUserByExistingId_userRemovedAndReturnedNoExceptions() {
        User user = repository.addUser(VALID_USER_1.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.removeUser(user.getId()),
                "User should be returned for existing id"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                repository.getAllUsers().size(),
                "User should be removed"
        );
    }

    @Test
    @DisplayName("Remove non-existing user by id")
    public void removeUser_getUserByNonExistingId_NotFoundException() {
        repository.addUser(VALID_USER_1.clone());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> repository.removeUser(NON_EXISTING_ID),
                "Should not return user for non-existing id"
        );

        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ONE,
                repository.getAllUsers().size(),
                "Other user should not be removed"
        );
    }

    @Test
    @DisplayName("Add user without name set")
    public void addUser_addUserWithoutNameSet_userAddedNoExceptionsAndLoginSetAsName() {
        User userWithoutNameSet = VALID_USER_1.clone();
        userWithoutNameSet.setName(null);

        Assertions.assertDoesNotThrow(
                () -> repository.addUser(userWithoutNameSet),
                "User should be added without exceptions"
        );
        Assertions.assertFalse(repository.getAllUsers().isEmpty(), "Repository should not be empty");
        User savedUser = repository.getAllUsers().getFirst();
        Assertions.assertEquals(VALID_LOGIN_1, savedUser.getName(), "Login should be set as name");
    }

    @Test
    @DisplayName("Add existing user")
    public void addUser_addExistingUser_throwDuplicateException() {
        User addedUser = repository.addUser(VALID_USER_1.clone());

        Assertions.assertThrows(DuplicatedDataException.class,
                () -> repository.addUser(addedUser),
                "Duplicate users are prohibited"
        );
    }

    @Test
    @DisplayName("Update non-existing user")
    public void updateUser_updateNonExistingUser_throwNotFoundException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> repository.updateUser(VALID_USER_1.clone()),
                "Can't update non-existing user!"
        );
    }

    @Test
    @DisplayName("Update existing user")
    public void updateUser_updateExistingUser_userUpdated() {
        User addedUser = repository.addUser(VALID_USER_1.clone());
        addedUser.setName(VALID_USER_NAME_2);

        Assertions.assertDoesNotThrow(
                () -> repository.updateUser(addedUser),
                "User should be updated"
        );
    }

    @Test
    @DisplayName("Get list of all users")
    public void getAllUsers_getAllUsers_listOfUsers() {
        repository.addUser(VALID_USER_1.clone());
        repository.addUser(VALID_USER_2.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.getAllUsers(),
                "Users should be returned without exceptions"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllUsers().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }
}
