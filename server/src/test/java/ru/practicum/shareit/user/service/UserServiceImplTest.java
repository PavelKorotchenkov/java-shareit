package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Captor
	private ArgumentCaptor<User> argumentCaptor;

	@Test
	void save_whenInvoked_returnUserDto() {
		long userId = 1L;
		UserCreateDto userToSaveCreateDto = UserCreateDto.builder().email("email@mail.ru").name("name").build();
		User userEntity = UserDtoMapper.ofUserCreateDto(userToSaveCreateDto);
		User savedUser = UserDtoMapper.ofUserCreateDto(userToSaveCreateDto);
		savedUser.setId(userId);
		when(userRepository.save(userEntity)).thenReturn(savedUser);

		UserDto actualUser = userService.save(userToSaveCreateDto);

		assertEquals(savedUser.getId(), actualUser.getId());
		assertEquals(savedUser.getName(), actualUser.getName());
		assertEquals(savedUser.getEmail(), actualUser.getEmail());
	}

	@Test
	void getAll_whenInvoked_thenReturnListOfUserDto() {
		List<UserDto> expectedList = List.of(UserDto.builder().name("name").build());
		List<User> expectedListUser = expectedList.stream().map(UserDtoMapper::ofUserDto).collect(Collectors.toList());
		when(userRepository.findAll()).thenReturn(expectedListUser);

		List<UserDto> actualList = userService.getAll();
		assertEquals(expectedList.size(), actualList.size());
		assertEquals(expectedList.get(0).getName(), actualList.get(0).getName());
	}

	@Test
	void getById_whenFound_thenReturnUser() {
		long userId = 1L;
		User expectedUser = new User();
		expectedUser.setId(userId);
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));

		User actualUser = UserDtoMapper.ofUserDto(userService.getById(userId));
		assertEquals(expectedUser, actualUser);
	}

	@Test
	void getById_whenNotFound_thenNotFoundExceptionThrown() {
		long userId = 0L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> userService.getById(userId));
	}

	@Test
	void update_whenUserFoundAndUpdatingAllFields_thenUpdateAllFields() {
		long userId = 1L;
		User oldUser = User.builder().id(userId).email("email").name("name").build();

		UserUpdateDto updateUserDto = UserUpdateDto
				.builder().email("email2").name("name2").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
		when(userRepository.save(any())).thenReturn(User.builder().id(userId).email("email2").name("name2").build());

		userService.update(userId, updateUserDto);

		verify(userRepository, times(1)).findById(userId);
		verify(userRepository).save(argumentCaptor.capture());
		User savedUser = argumentCaptor.getValue();

		assertEquals("email2", savedUser.getEmail());
		assertEquals("name2", savedUser.getName());
	}

	@Test
	void update_whenUserFoundAndUpdatingEmail_thenUpdateEmail() {
		long userId = 1L;
		User oldUser = User.builder().id(userId).email("email").name("name").build();

		UserUpdateDto newUser = UserUpdateDto.builder().email("email2").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
		when(userRepository.save(any())).thenReturn(User.builder().id(userId).email("email2").name("name").build());

		userService.update(userId, newUser);

		verify(userRepository, times(1)).findById(userId);
		verify(userRepository).save(argumentCaptor.capture());
		User savedUser = argumentCaptor.getValue();

		assertEquals("email2", savedUser.getEmail());
		assertEquals("name", savedUser.getName());
	}

	@Test
	void update_whenUserFoundAndUpdatingName_thenUpdateName() {
		long userId = 1L;
		User oldUser = User.builder().id(userId).email("email").name("name").build();
		UserUpdateDto newUser = UserUpdateDto.builder().name("name2").build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
		when(userRepository.save(any())).thenReturn(User.builder().id(userId).email("email").name("name2").build());

		userService.update(userId, newUser);

		verify(userRepository, times(1)).findById(userId);
		verify(userRepository).save(argumentCaptor.capture());
		User savedUser = argumentCaptor.getValue();

		assertEquals("email", savedUser.getEmail());
		assertEquals("name2", savedUser.getName());
	}

	@Test
	void update_whenUserNotFound_thenThrowNotFoundException() {
		long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> userService.getById(userId));
	}

	@Test
	void deleteById() {
		long userId = 1L;
		userService.deleteById(userId);
		verify(userRepository, times(1)).deleteById(userId);
	}
}