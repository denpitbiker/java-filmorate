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
    public void addUser_addCorrectUser_userAddedAndReturned() {
        Assertions.assertTrue(
                () -> repository.addUser(VALID_USER_1.clone()).isPresent(),
                "User should be added and returned"
        );
        Assertions.assertTrue(
                () -> repository.addUser(VALID_USER_2.clone()).isPresent(),
                "Second user should be added and returned"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllUsers().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Get existing user by id")
    public void getUser_getUserByExistingId_userReturned() {
        User user = repository.addUser(VALID_USER_1.clone()).get();

        Assertions.assertTrue(
                () -> repository.getUser(user.getId()).isPresent(),
                "User should be returned for existing id"
        );
    }

    @Test
    @DisplayName("Remove existing user by id")
    public void removeUser_removeUserByExistingId_userRemovedAndReturned() {
        User user = repository.addUser(VALID_USER_1.clone()).get();

        Assertions.assertTrue(
                () -> repository.removeUser(user.getId()).isPresent(),
                "User should be returned for existing id"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                repository.getAllUsers().size(),
                "User should be removed"
        );
    }

    @Test
    @DisplayName("Add user without name set")
    public void addUser_addUserWithoutNameSet_userAddedNAndLoginSetAsName() {
        User userWithoutNameSet = VALID_USER_1.clone();
        userWithoutNameSet.setName(null);

        Assertions.assertTrue(
                () -> repository.addUser(userWithoutNameSet).isPresent(),
                "User should be added and returned"
        );
        Assertions.assertFalse(repository.getAllUsers().isEmpty(), "Repository should not be empty");
        User savedUser = repository.getAllUsers().getFirst();
        Assertions.assertEquals(VALID_LOGIN_1, savedUser.getName(), "Login should be set as name");
    }

    @Test
    @DisplayName("Update existing user")
    public void updateUser_updateExistingUser_userUpdatedAndReturned() {
        User addedUser = repository.addUser(VALID_USER_1.clone()).get();
        addedUser.setName(VALID_USER_NAME_2);

        Assertions.assertTrue(
                () -> repository.updateUser(addedUser).isPresent(),
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

    @Test
    @DisplayName("Check existing user id")
    public void hasUserId_checkExistingUserIdInStorage_returnedTrue() {
        Long id = repository.addUser(VALID_USER_1.clone()).get().getId();
        Assertions.assertTrue(repository.hasUserId(id), "Repository should contain added user id");
    }

    @Test
    @DisplayName("Check non-existing user id")
    public void hasUserId_checkNonExistingUserIdInStorage_returnedFalse() {
        Assertions.assertFalse(repository.hasUserId(NON_EXISTING_ID), "Repository should not contain unknown user id");
    }
}
