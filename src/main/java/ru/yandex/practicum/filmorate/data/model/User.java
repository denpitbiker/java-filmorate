package ru.yandex.practicum.filmorate.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User implements Cloneable {
    Long id;
    String email;
    String login;
    String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
    Set<Long> friends;

    @Override
    public User clone() {
        User cloned = new User(id, email, login, name, birthday, new HashSet<>());
        cloned.getFriends().addAll(friends);
        return cloned;
    }
}
