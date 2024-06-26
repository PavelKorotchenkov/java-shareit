package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Builder
public class UserCreateDto {
	@NotEmpty(message = "Имя пользователя не может быть пустым")
	private String name;
	@NotEmpty(message = "Эл.почта пользователя не может быть пустой")
	@Email(message = "Ошибка в адресе электронной почты")
	private String email;
}