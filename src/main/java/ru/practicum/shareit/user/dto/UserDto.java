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
public class UserDto {
	private long id;
	@NotEmpty
	private String name;
	@NotEmpty
	@Email
	private String email;
}
