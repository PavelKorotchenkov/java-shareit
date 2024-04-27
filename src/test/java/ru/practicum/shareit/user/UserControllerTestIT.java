package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTestIT {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;


	@SneakyThrows
	@Test
	void saveNewUser_whenValidUser_thenReturnOk() {
		UserCreateDto userCreateDto = UserCreateDto.builder().name("name").email("mail@mail.ru").build();
		UserDto userDto = UserDto.builder().id(1L).name("name").email("mail@mail.ru").build();

		when(userService.save(any())).thenReturn(userDto);

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("name"))
				.andExpect(jsonPath("$.email").value("mail@mail.ru"));
	}

	@SneakyThrows
	@Test
	void saveNewUser_whenUserWithoutEmail_thenReturnBadRequest() {
		UserCreateDto userCreateDto = UserCreateDto.builder().name("name").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userService, never()).save(userCreateDto);
	}

	@SneakyThrows
	@Test
	void saveNewUser_whenUserWithNotValidEmail_thenReturnBadRequest() {
		UserCreateDto userCreateDto = UserCreateDto.builder().email("email").name("name").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userService, never()).save(userCreateDto);
	}

	@SneakyThrows
	@Test
	void saveNewUser_whenUserWithoutName_thenReturnBadRequest() {
		UserCreateDto userCreateDto = UserCreateDto.builder().email("mail@mail.ru").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userService, never()).save(userCreateDto);
	}

	@SneakyThrows
	@Test
	void getAllUsers() {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());

		verify(userService).getAll();
	}

	@SneakyThrows
	@Test
	void getUserById() {
		long id = 0L;
		mockMvc.perform(get("/users/{id}", id))
				.andExpect(status().isOk());

		verify(userService).getById(id);
	}

	@SneakyThrows
	@Test
	void getUserById_whenNoUser_thenReturnNotFound() {
		long id = 0L;
		when(userService.getById(id)).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/users/{id}", id))
				.andExpect(status().isNotFound());

		verify(userService).getById(id);
	}

	@SneakyThrows
	@Test
	void updateUser_whenValidParams_thenStatusOk() {
		long id = 1L;
		UserUpdateDto userUpdateDto = UserUpdateDto.builder().email("mail@mail.ru").name("name").build();

		mockMvc.perform(patch("/users/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andExpect(status().isOk());

		verify(userService).update(any(), any());
	}

	@SneakyThrows
	@Test
	void updateUser_whenEmailNotValid_thenReturnBadRequest() {
		Long id = 1L;
		UserUpdateDto userUpdateDto = UserUpdateDto.builder().email("mail").name("name").build();

		mockMvc.perform(patch("/users/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andExpect(status().isBadRequest());

		verify(userService, never()).update(id, userUpdateDto);
	}

	@SneakyThrows
	@Test
	void deleteUser_whenInvoked_thenReturnOk() {
		Long id = 1L;
		mockMvc.perform(delete("/users/{id}", id))
				.andExpect(status().is2xxSuccessful());
		verify(userService, times(1)).deleteById(id);
	}
}