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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto save(UserCreateDto userCreateDto) {
		User user = UserDtoMapper.ofUserCreateDto(userCreateDto);
		return UserDtoMapper.toUserDto(userRepository.save(user));
	}

	@Override
	public List<UserDto> getAll() {
		return userRepository.findAll()
				.stream()
				.map(UserDtoMapper::toUserDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getById(Long id) {
		return UserDtoMapper.toUserDto(userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует: " + id)));
	}

	@Override
	public UserDto update(Long id, UserUpdateDto userUpdateDto) {
		User currentUser = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует: " + id));
		UserDto currentUserDto = UserDtoMapper.toUserDto(currentUser);
		User userUpdate = UserDtoMapper.ofUserUpdateDto(userUpdateDto);
		userUpdate.setId(id);

		if (userUpdate.getName() == null) {
			userUpdate.setName(currentUserDto.getName());
		}

		if (userUpdate.getEmail() == null) {
			userUpdate.setEmail(currentUserDto.getEmail());
		}

		return UserDtoMapper.toUserDto(userRepository.save(userUpdate));
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}
}
