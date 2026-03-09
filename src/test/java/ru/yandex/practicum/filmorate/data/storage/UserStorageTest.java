package ru.yandex.practicum.filmorate.data.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public abstract class UserStorageTest {

    private final UserStorage repository;

    protected UserStorageTest(UserStorage repository) {
        this.repository = repository;
    }

    @Test
    @DisplayName("Add correct user")
    public void addUser_addCorrectUser_userAddedAndReturned() {
        Assertions.assertNotNull(
                repository.addUser(VALID_USER_1.clone()),
                "User should be added and returned"
        );
        Assertions.assertNotNull(
                repository.addUser(VALID_USER_2.clone()),
                "Second user should be added and returned"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllUsers().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Get existing user by id")
    public void getUser_getUserByExistingId_userReturned() {
        User user = repository.addUser(VALID_USER_1.clone());

        Assertions.assertTrue(
                () -> repository.getUser(user.getId()).isPresent(),
                "User should be returned for existing id"
        );
    }

    @Test
    @DisplayName("Remove existing user by id")
    public void removeUser_removeUserByExistingId_userRemovedAndReturned() {
        User user = repository.addUser(VALID_USER_1.clone());

        Assertions.assertNotNull(
                repository.removeUser(user.getId()),
                "User should be returned for existing id"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                repository.getAllUsers().size(),
                "User should be removed"
        );
    }

    @Test
    @DisplayName("Update existing user")
    public void updateUser_updateExistingUser_userUpdatedAndReturned() {
        User addedUser = repository.addUser(VALID_USER_1.clone());
        addedUser.setName(VALID_USER_NAME_2);

        Assertions.assertNotNull(
                repository.updateUser(addedUser),
                "User should be updated"
        );
    }

    @Test
    @DisplayName("Get list of all users")
    public void getAllUsers_getAllUsers_listOfUsers() {
        repository.addUser(VALID_USER_1.clone());
        repository.addUser(VALID_USER_2.clone());

        Assertions.assertDoesNotThrow(
                repository::getAllUsers,
                "Users should be returned without exceptions"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllUsers().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Check existing user id")
    public void hasUserId_checkExistingUserIdInStorage_returnedTrue() {
        Long id = repository.addUser(VALID_USER_1.clone()).getId();
        Assertions.assertTrue(repository.hasUserId(id), "Repository should contain added user id");
    }

    @Test
    @DisplayName("Check non-existing user id")
    public void hasUserId_checkNonExistingUserIdInStorage_returnedFalse() {
        Assertions.assertFalse(repository.hasUserId(NON_EXISTING_ID), "Repository should not contain unknown user id");
    }
}
