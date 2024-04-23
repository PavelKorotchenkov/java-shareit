package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Test
	void saveNewUser_whenInvoked_thenResponseStatusOkWithSavedUserInBody() {
		UserCreateDto userToSave = UserCreateDto.builder()
				.email("email@email.ru")
				.name("name")
				.build();

		when(userService.save(userToSave)).thenReturn(UserDto.builder()
				.id(1L)
				.email("email@email.ru")
				.name("name")
				.build());

		ResponseEntity<UserDto> response = userController.saveNewUser(userToSave);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(userToSave.getEmail(), Objects.requireNonNull(response.getBody()).getEmail());
		assertEquals(userToSave.getName(), Objects.requireNonNull(response.getBody()).getName());
	}

	@SneakyThrows
	@Test
	void getAllUsers_whenInvoked_thenResponseStatusOkWithUserCollectionInBody() {
		List<UserDto> expectedUsers = List.of(UserDto.builder().build());
		when(userService.getAll()).thenReturn(expectedUsers);

		ResponseEntity<List<UserDto>> response = userController.getAllUsers();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedUsers, response.getBody());
	}

	@Test
	void getUserById_whenFound_thenReturnUser() {
		long userId = 0L;
		UserDto expectedUser = UserDto.builder().build();

		when(userService.getById(userId)).thenReturn(expectedUser);

		ResponseEntity<UserDto> actualUser = userController.getUserById(userId);

		assertEquals(HttpStatus.OK, actualUser.getStatusCode());
		assertEquals(expectedUser, actualUser.getBody());
	}

	@Test
	void updateUser() {
		long userId = 1L;

		UserUpdateDto userToUpdate = UserUpdateDto.builder()
				.name("name2")
				.email("email2")
				.build();

		UserDto updatedUser = UserDto.builder()
				.name("name2")
				.email("email2")
				.build();

		when(userService.update(userId, userToUpdate)).thenReturn(updatedUser);

		ResponseEntity<UserDto> actualUser = userController.updateUser(userId, userToUpdate);
		assertEquals(HttpStatus.OK, actualUser.getStatusCode());
		assertEquals(updatedUser, actualUser.getBody());
	}

	@Test
	void deleteUser() {
		Long userId = 1L;
		userController.deleteUser(userId);
		verify(userService, times(1)).deleteById(userId);
	}
}