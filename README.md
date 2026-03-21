# java-filmorate
Template repository for Filmorate project.

public class Film implements Cloneable {
Long id;
@NotBlank(message = "Name should not be blank!")
String name;
@NullOrNotBlank
@Length(max = 200, message = "Description must be <= 200 symbols!")
String description;
@DateInRange(startDate = "1895-12-28")
@JsonFormat(pattern = "yyyy-MM-dd")
LocalDate releaseDate;
@Positive
@NotNull
@JsonProperty("duration")
Long durationMinutes;
final Set<Long> likes = new HashSet<>();
}

public class User implements Cloneable {
Long id;
@Email
@NotBlank
String email;
@NotBlank
String login;
String name;
@PastOrPresent
@NotNull
@JsonFormat(pattern = "yyyy-MM-dd")
LocalDate birthday;
final Set<Long> friends = new HashSet<>();
}

User(id: Integer Autoincrement notnull, email varchar notnull, login varchar notnull, name varchar, birthday date)
Film(id: Integer Autoincrement, name varchar, description varchar, name varchar, birthday date)
