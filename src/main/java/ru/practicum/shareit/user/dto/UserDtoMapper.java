package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserDtoMapper {
	public static UserDto toUserDto(User user) {
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}

	public static UserDto toUserDtoId(User user) {
		return UserDto.builder()
				.id(user.getId())
				.build();
	}

	public static User ofUserDto(UserDto userDto) {
		return User.builder()
				.id(userDto.getId())
				.name(userDto.getName())
				.email(userDto.getEmail())
				.build();
	}

	public static User ofUserCreateDto(UserCreateDto userCreateDto) {
		return User.builder()
				.name(userCreateDto.getName())
				.email(userCreateDto.getEmail())
				.build();
	}

	public static User ofUserUpdateDto(UserUpdateDto userUpdateDto) {
		return User.builder()
				.name(userUpdateDto.getName())
				.email(userUpdateDto.getEmail())
				.build();
	}
}
