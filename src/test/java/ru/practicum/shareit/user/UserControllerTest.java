package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

		UserDto response = userController.saveNewUser(userToSave);

		assertEquals(userToSave.getEmail(), Objects.requireNonNull(response).getEmail());
		assertEquals(userToSave.getName(), Objects.requireNonNull(response).getName());
	}

	@SneakyThrows
	@Test
	void getAllUsers_whenInvoked_thenResponseStatusOkWithUserCollectionInBody() {
		List<UserDto> expectedUsers = List.of(UserDto.builder().build());
		when(userService.getAll()).thenReturn(expectedUsers);

		List<UserDto> response = userController.getAllUsers();

		assertEquals(expectedUsers, response);
	}

	@Test
	void getUserById_whenFound_thenReturnUser() {
		long userId = 0L;
		UserDto expectedUser = UserDto.builder().build();

		when(userService.getById(userId)).thenReturn(expectedUser);

		UserDto actualUser = userController.getUserById(userId);

		assertEquals(expectedUser, actualUser);
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

		UserDto actualUser = userController.updateUser(userId, userToUpdate);
		assertEquals(updatedUser, actualUser);
	}

	@Test
	void deleteUser() {
		Long userId = 1L;
		userController.deleteUser(userId);
		verify(userService, times(1)).deleteById(userId);
	}
}