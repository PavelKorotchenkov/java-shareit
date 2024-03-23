package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping
	public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
		log.info("Получен запрос на добавление пользователя: {}", userDto);
		User user = UserDtoMapper.toUser(userDto);
		User savedUser = userService.save(user);
		UserDto savedUserDto = UserDtoMapper.toDto(savedUser);
		log.info("Отработан запрос на добавление пользователя: {}", savedUserDto);
		return savedUserDto;
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		log.info("Получен запрос на получение всех пользователей");
		List<UserDto> allUsersDto = userService.getAll()
				.stream()
				.map(UserDtoMapper::toDto)
				.collect(Collectors.toList());
		log.info("Отработан запрос на получение всех пользователей");
		return allUsersDto;
	}

	@GetMapping("/{id}")
	public UserDto getUserById(@PathVariable Long id) {
		log.info("Получен запрос на получение пользователя с id: {}", id);
		User userById = userService.getById(id);
		UserDto userDto = UserDtoMapper.toDto(userById);
		log.info("Отработан запрос на получение пользователя с id: {}", id);
		return userDto;
	}

	@PatchMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id,
							  @RequestBody UserDto userDto) {
		log.info("Получен запрос на обновление пользователя: {}, id: {}", userDto, id);
		User user = UserDtoMapper.toUser(userDto);
		User updatedUser = userService.update(id, user);
		UserDto updatedUserDto = UserDtoMapper.toDto(updatedUser);
		log.info("Отработан запрос на обновление пользователя: {}, id: {}", updatedUserDto, id);
		return updatedUserDto;
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		log.info("Получен запрос на удаление пользователя с id: {}", id);
		userService.deleteById(id);
		log.info("Отработан запрос на удаление пользователя с id: {}", id);
	}
}
