package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
	UserDto save(UserCreateDto userCreateDto);

	List<UserDto> getAll();

	UserDto getById(Long id);

	UserDto update(Long id, UserUpdateDto userUpdateDto);

	void deleteById(Long id);
}
