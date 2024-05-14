package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@Builder
public class UserUpdateDto {
	private String name;
	@Email(message = "Ошибка в адресе электронной почты")
	private String email;
}