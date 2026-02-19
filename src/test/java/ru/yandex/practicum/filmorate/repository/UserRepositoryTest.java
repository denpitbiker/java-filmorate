package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
public class UserRepositoryTest {
    private static final int EXPECTED_REPOSITORY_SIZE_TWO = 2;

    @Autowired
    private UserRepository repository;

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
