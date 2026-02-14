package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    Long id;
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date birthday;
}
