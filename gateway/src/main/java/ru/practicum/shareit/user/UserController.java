package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

	private final UserClient userClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserCreateDto userCreateDto) {
		log.info("Получен запрос на добавление пользователя: {}", userCreateDto);
		return userClient.saveNewUser(userCreateDto);
	}

	@GetMapping
	public ResponseEntity<Object> getAllUsers() {
		log.info("Получен запрос на получение всех пользователей");
		return userClient.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable Long id) {
		log.info("Получен запрос на получение пользователя с id: {}", id);
		return userClient.getUserById(id);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id,
											 @Valid @RequestBody UserUpdateDto userUpdateDto) {
		log.info("Получен запрос на обновление пользователя: {}, id: {}", userUpdateDto, id);
		return userClient.updateUser(id, userUpdateDto);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		log.info("Получен запрос на удаление пользователя с id: {}", id);
		return userClient.deleteById(id);
	}
}
