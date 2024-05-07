package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	public static final Long USER_ID = 1L;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemRequestClient client;

	@SneakyThrows
	@Test
	void addRequest_whenValidDescription_thenAddNewRequest() {
		ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
				.description("description").build();

		mockMvc.perform(post("/requests")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequestCreateDto)))
				.andExpect(status().is2xxSuccessful());
	}

	@SneakyThrows
	@Test
	void addRequest_whenNotValidDescription_thenThrowBadRequest() {
		ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
				.description("").build();

		mockMvc.perform(post("/requests")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequestCreateDto)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void getRequests() {
		mockMvc.perform(get("/requests")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getOtherRequests() {
		mockMvc.perform(get("/requests/all")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getRequestById() {
		long requestId = 1L;
		List<ItemDto> list = Collections.emptyList();
		ItemRequestResponseDto expectedItemRequest = ItemRequestResponseDto.builder()
				.id(1L)
				.description("description")
				.created("2024-01-01T10:10:10")
				.items(list).build();

		mockMvc.perform(get("/requests/{requestId}", requestId)
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(expectedItemRequest)))
				.andExpect(status().isOk());
	}
}