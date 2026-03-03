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
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private static final String NOT_FOUND_USER_FAIL_MSG = "NotFoundException should be thrown for unknown user";

    @Autowired
    private InMemoryUserStorage storage;

    @Autowired
    private UserService service;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    public void setUp() {
        userId1 = storage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        userId2 = storage.addUser(TestStubs.VALID_USER_2.clone()).get().getId();
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
    @DisplayName("Add friend to unknown user")
    public void addFriend_addFriendToNonExistingUser_throwNotFoundException() {
        service.addFriend(userId1, userId2);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.addFriend(NON_EXISTING_ID, userId2),
                NOT_FOUND_USER_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Add unknown friend to user")
    public void addFriend_addNonExistingFriendToUser_throwNotFoundException() {
        service.addFriend(userId1, userId2);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.addFriend(userId2, NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
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
    @DisplayName("Remove friend from unknown user")
    public void removeFriend_removeFriendFromNonExistingUser_throwNotFoundException() {
        service.addFriend(userId1, userId2);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(NON_EXISTING_ID, userId2),
                NOT_FOUND_USER_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Remove unknown friend from user")
    public void removeFriend_removeNonExistingFriendFromUser_throwNotFoundException() {
        service.addFriend(userId1, userId2);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(userId2, NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
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
    @DisplayName("Get unknown user friends")
    public void getUserFriends_getNonExistingUserFriends_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getUserFriends(NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Get common friends")
    public void getCommonFriends_validUsers_commonFriendsReturned() {
        Long userId3 = storage.addUser(VALID_USER_3).get().getId();
        service.addFriend(userId1, userId3);
        service.addFriend(userId2, userId3);

        Collection<User> commonFriends = Assertions.assertDoesNotThrow(
                () -> service.getCommonFriends(userId1, userId2),
                "Getting common friends should not throw exceptions"
        );

        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_ONE, commonFriends.size(), "There should be one common friend");
        Assertions.assertTrue(commonFriends.stream().anyMatch(user -> user.getId().equals(userId3)),
                "User 3 should be the common friend between User 1 and User 2");
    }

    @Test
    @DisplayName("Get common friends by non existing id")
    public void getCommonFriends_getCommonFriendWithNonExistingFriendById_throwNotFoundException() {
        Long userId3 = storage.addUser(VALID_USER_3).get().getId();
        service.addFriend(userId1, userId3);
        service.addFriend(userId2, userId3);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(userId3, NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(NON_EXISTING_ID, userId3),
                NOT_FOUND_USER_FAIL_MSG
        );
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
    @DisplayName("Add friend by non existing id")
    public void addFriend_addNonExistingFriendById_throwNotFoundException() {
        Long userId3 = storage.addUser(VALID_USER_3).get().getId();
        service.addFriend(userId1, userId3);
        service.addFriend(userId2, userId3);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.addFriend(userId3, NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.addFriend(NON_EXISTING_ID, userId3),
                NOT_FOUND_USER_FAIL_MSG
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
    @DisplayName("Remove friend by non existing id")
    public void removeFriend_removeNonExistingFriendById_throwNotFoundException() {
        Long userId3 = storage.addUser(VALID_USER_3).get().getId();
        service.addFriend(userId1, userId3);
        service.addFriend(userId2, userId3);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(userId3, NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
        );
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(NON_EXISTING_ID, userId3),
                NOT_FOUND_USER_FAIL_MSG
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
        User added = storage.addUser(VALID_USER_1.clone()).get();
        Assertions.assertDoesNotThrow(
                () -> service.getUser(added.getId()),
                "User should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get user by non existing id")
    public void getUser_getNonExistingUserById_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getUser(NON_EXISTING_ID),
                NOT_FOUND_USER_FAIL_MSG
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
    @DisplayName("Add user duplicate")
    public void addUser_addDuplicateUser_throwDuplicatedDataException() {
        User user = service.addUser(VALID_USER_1.clone());
        Assertions.assertThrows(
                DuplicatedDataException.class,
                () -> service.addUser(user),
                "DuplicatedDataException should be thrown for duplicate user"
        );
    }

    @Test
    @DisplayName("Update user")
    public void updateUser_updateExistingUser_userUpdatedNoExceptions() {
        User userToUpdate = storage.addUser(VALID_USER_1.clone()).get();
        userToUpdate.setEmail(VALID_EMAIL_2);
        Assertions.assertDoesNotThrow(
                () -> service.updateUser(userToUpdate),
                "User should be updated without exceptions"
        );
    }

    @Test
    @DisplayName("Update non-existing user")
    public void updateUser_updateNonExistingUser_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.updateUser(VALID_USER_1.clone()),
                NOT_FOUND_USER_FAIL_MSG
        );
    }
}
