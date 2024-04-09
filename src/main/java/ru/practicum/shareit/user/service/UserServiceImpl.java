package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
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

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

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
		return userRepository.findAll()
				.stream()
				.map(UserDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getById(Long id) {
		return UserDtoMapper.toDto(getUser(id));
	}

	@Override
	public UserDto update(Long id, UserUpdateDto userUpdateDto) {
		UserDto currentUserDto = UserDtoMapper.toDto(getUser(id));
		User userUpdate = UserDtoMapper.toUserUpdate(userUpdateDto);
		userUpdate.setId(id);

		if (userUpdate.getName() == null) {
			userUpdate.setName(currentUserDto.getName());
		}

		if (userUpdate.getEmail() == null) {
			userUpdate.setEmail(currentUserDto.getEmail());
		}

		return UserDtoMapper.toDto(userRepository.save(userUpdate));
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	private User getUser(Long id) {
		Optional<User> optUser = userRepository.findById(id);
		return optUser.orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует: " + id));
	}
}
