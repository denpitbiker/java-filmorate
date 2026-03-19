package ru.yandex.practicum.filmorate.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User implements Cloneable {
    Long id;
    String email;
    String login;
    String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;

    @Override
    public User clone() {
        return new User(id, email, login, name, birthday);
    }
}
