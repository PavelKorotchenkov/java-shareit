package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class UserControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;


	@Test
	void saveNewUser_whenValidUser_thenReturnOk() throws Exception {
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

	@Test
	void getAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());

		verify(userService).getAll();
	}

	@Test
	void getUserById() throws Exception {
		long id = 1L;
		mockMvc.perform(get("/users/{id}", id))
				.andExpect(status().isOk());

		verify(userService).getById(id);
	}

	@Test
	void getUserById_whenNoUser_thenReturnNotFound() throws Exception {
		long id = 0L;
		when(userService.getById(id)).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/users/{id}", id))
				.andExpect(status().isNotFound());

		verify(userService).getById(id);
	}

	@Test
	void updateUser_whenValidParams_thenStatusOk() throws Exception {
		long id = 1L;
		UserUpdateDto userUpdateDto = UserUpdateDto.builder().email("mail@mail.ru").name("name").build();

		mockMvc.perform(patch("/users/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andExpect(status().isOk());

		verify(userService).update(any(), any());
	}

	@Test
	void deleteUser_whenInvoked_thenReturnOk() throws Exception {
		Long id = 1L;
		mockMvc.perform(delete("/users/{id}", id))
				.andExpect(status().is2xxSuccessful());
		verify(userService, times(1)).deleteById(id);
	}
}