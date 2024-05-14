package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserClient userClient;

	@Test
	void saveNewUser_whenUserValidParams_thenReturnStatusOk() throws Exception {
		UserCreateDto userCreateDto = UserCreateDto.builder().email("email@gmail.com").name("name").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void saveNewUser_whenUserWithNotValidEmail_thenReturnBadRequest() throws Exception {
		UserCreateDto userCreateDto = UserCreateDto.builder().email("email").name("name").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userClient, never()).saveNewUser(any());
	}

	@Test
	void saveNewUser_whenUserWithoutName_thenReturnBadRequest() throws Exception {
		UserCreateDto userCreateDto = UserCreateDto.builder().email("mail@mail.ru").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userClient, never()).saveNewUser(any());
	}

	@Test
	void saveNewUser_whenUserWithoutEmail_thenReturnBadRequest() throws Exception {
		UserCreateDto userCreateDto = UserCreateDto.builder().name("name").build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isBadRequest());

		verify(userClient, never()).saveNewUser(any());
	}

	@Test
	void getAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}

	@Test
	void getUserById() throws Exception {
		mockMvc.perform(get("/users/{id}", anyLong()))
				.andExpect(status().isOk());
	}

	@Test
	void updateUser_whenUserWithValidParams_thenReturnStatusOk() throws Exception {
		long id = 1L;
		UserUpdateDto userUpdateDto = UserUpdateDto.builder().email("mailupd@gmail.com").name("nameupd").build();

		mockMvc.perform(patch("/users/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void updateUser_whenUserWithEmailNotValid_thenReturnBadRequest() throws Exception {
		long id = 1L;
		UserUpdateDto userUpdateDto = UserUpdateDto.builder().email("mail").name("name").build();

		mockMvc.perform(patch("/users/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andExpect(status().isBadRequest());

		verify(userClient, never()).updateUser(anyLong(), any());
	}

	@Test
	void deleteUser_whenInvoked_thenReturnOk() throws Exception {
		mockMvc.perform(delete("/users/{id}", anyLong()))
				.andExpect(status().is2xxSuccessful());
	}
}