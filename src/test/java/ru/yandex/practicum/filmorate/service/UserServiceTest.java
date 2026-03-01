package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.TestStubs;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private InMemoryUserStorage storage;

    @Autowired
    private UserService service;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    public void setUp() {
        userId1 = storage.addUser(TestStubs.VALID_USER_1.clone()).getId();
        userId2 = storage.addUser(TestStubs.VALID_USER_2.clone()).getId();
    }

    @Test
    @DisplayName("Add friend")
    public void addFriend_validUsers_friendAdded() {
        Assertions.assertDoesNotThrow(
                () -> service.addFriend(userId1, userId2),
                "Adding a friend should not throw exceptions"
        );

        Collection<User> userFriends = service.getUserFriends(userId1);
        Assertions.assertTrue(userFriends.stream().anyMatch(user -> user.getId().equals(userId2)),
                "User 2 should be a friend of User 1");
    }

    @Test
    @DisplayName("Remove friend")
    public void removeFriend_validUsers_friendRemoved() {
        service.addFriend(userId1, userId2);

        Assertions.assertDoesNotThrow(
                () -> service.removeFriend(userId1, userId2),
                "Removing a friend should not throw exceptions"
        );

        Collection<User> userFriends = service.getUserFriends(userId1);
        Assertions.assertFalse(userFriends.stream().anyMatch(user -> user.getId().equals(userId2)),
                "User 2 should not be a friend of User 1");
    }

    @Test
    @DisplayName("Get user friends")
    public void getUserFriends_validUser_friendsReturned() {
        service.addFriend(userId1, userId2);

        Collection<User> userFriends = Assertions.assertDoesNotThrow(
                () -> service.getUserFriends(userId1),
                "Getting friends should not throw exceptions"
        );

        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_ONE, userFriends.size(), "User 1 should have one friend");
        Assertions.assertTrue(userFriends.stream().anyMatch(user -> user.getId().equals(userId2)),
                "User 2 should be in the friends list of User 1");
    }

    @Test
    @DisplayName("Get common friends")
    public void getCommonFriends_validUsers_commonFriendsReturned() {
        Long userId3 = storage.addUser(VALID_USER_3).getId();
        service.addFriend(userId1, userId3);
        service.addFriend(userId2, userId3);

        Collection<User> commonFriends = Assertions.assertDoesNotThrow(
                () -> service.getCommonFriends(userId1, userId2),
                "Getting common friends should not throw exceptions"
        );

        Assertions.assertEquals(1, commonFriends.size(), "There should be one common friend");
        Assertions.assertTrue(commonFriends.stream().anyMatch(user -> user.getId().equals(userId3)),
                "User 3 should be the common friend between User 1 and User 2");
    }

    @Test
    @DisplayName("Add friend with the same IDs")
    public void addFriend_sameId_throwsValidationException() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFriend(userId1, userId1),
                "Adding a friend with the same ID should throw ValidationException"
        );
    }

    @Test
    @DisplayName("Remove friend with the same IDs")
    public void removeFriend_sameId_throwsValidationException() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.removeFriend(userId1, userId1),
                "Removing a friend with the same ID should throw ValidationException"
        );
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsers_getAllUsers_returnedAllUsers() {
        Assertions.assertDoesNotThrow(
                () -> service.getAllUsers(),
                "Users should be returned without exceptions"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                service.getAllUsers().size(),
                "Should be exact two users"
        );
    }

    @Test
    @DisplayName("Get user by id")
    public void getUser_getExistingUserById_returnedUser() {
        User added = storage.addUser(VALID_USER_1.clone());
        Assertions.assertDoesNotThrow(
                () -> service.getUser(added.getId()),
                "User should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Add user")
    public void addUser_addNewUser_userAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> service.addUser(VALID_USER_1.clone()),
                "User should be added without exceptions"
        );
    }

    @Test
    @DisplayName("Update user")
    public void updateUser_updateExistingUser_userUpdatedNoExceptions() {
        User userToUpdate = storage.addUser(VALID_USER_1.clone());
        userToUpdate.setEmail(VALID_EMAIL_2);
        Assertions.assertDoesNotThrow(
                () -> service.updateUser(userToUpdate),
                "User should be updated without exceptions"
        );
    }
}
