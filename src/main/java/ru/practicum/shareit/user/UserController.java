package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping
	public UserDto saveNewUser(@Valid @RequestBody UserCreateDto userCreateDto) {
		log.info("Получен запрос на добавление пользователя: {}", userCreateDto);
		UserDto savedUser = userService.save(userCreateDto);
		log.info("Отработан запрос на добавление пользователя: {}", savedUser);
		return savedUser;
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		log.info("Получен запрос на получение всех пользователей");
		List<UserDto> allUsersDto = userService.getAll();
		log.info("Отработан запрос на получение всех пользователей");
		return allUsersDto;
	}

	@GetMapping("/{id}")
	public UserDto getUserById(@PathVariable Long id) {
		log.info("Получен запрос на получение пользователя с id: {}", id);
		UserDto userById = userService.getById(id);
		log.info("Отработан запрос на получение пользователя с id: {}", id);
		return userById;
	}

	@PatchMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id,
							  @RequestBody UserUpdateDto userUpdateDto) {
		log.info("Получен запрос на обновление пользователя: {}, id: {}", userUpdateDto, id);
		UserDto updatedUser = userService.update(id, userUpdateDto);
		log.info("Отработан запрос на обновление пользователя: {}, id: {}", updatedUser, id);
		return updatedUser;
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		log.info("Получен запрос на удаление пользователя с id: {}", id);
		userService.deleteById(id);
		log.info("Отработан запрос на удаление пользователя с id: {}", id);
	}
}
