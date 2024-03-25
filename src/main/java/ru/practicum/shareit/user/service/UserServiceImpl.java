package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto save(UserCreateDto userCreateDto) {
		User user = UserDtoMapper.toUserCreate(userCreateDto);
		return UserDtoMapper.toDto(userRepository.save(user));
	}

	@Override
	public List<UserDto> getAll() {
		return userRepository.getAll()
				.stream()
				.map(UserDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getById(Long id) {
		return UserDtoMapper.toDto(checkUser(id));
	}

	@Override
	public UserDto update(Long id, UserUpdateDto userUpdateDto) {
		UserDto currentUserDto = UserDtoMapper.toDto(checkUser(id));
		User userUpdate = UserDtoMapper.toUserUpdate(userUpdateDto);
		userUpdate.setId(id);

		if (userUpdate.getName() == null) {
			userUpdate.setName(currentUserDto.getName());
		}

		if (userUpdate.getEmail() == null) {
			userUpdate.setEmail(currentUserDto.getEmail());
		}

		return UserDtoMapper.toDto(userRepository.update(userUpdate));
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	private User checkUser(Long id) {
		Optional<User> optUser = userRepository.getById(id);
		return optUser.orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует: " + id));
	}
}
