package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserDto {
	private Long id;
	@NotNull(message = "Имя пользователя не может быть пустым")
	private String name;
	@NotNull(message = "Эл.почта пользователя не может быть пустой")
	@Email(message = "Ошибка в адресе электронной почты")
	private String email;
}
