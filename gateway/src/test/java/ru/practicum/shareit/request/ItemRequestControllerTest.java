package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
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
						.header(X_SHARER_USER_ID, 1L)
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
						.header(X_SHARER_USER_ID, 0L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequestCreateDto)))
				.andExpect(status().isBadRequest());
	}
}