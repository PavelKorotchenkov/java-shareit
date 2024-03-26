package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
	@Null
	private Long id;
	@NotNull(message = "Имя пользователя не может быть пустым")
	private String name;
	@NotNull(message = "Эл.почта пользователя не может быть пустой")
	@Email(message = "Ошибка в адресе электронной почты")
	private String email;
}
