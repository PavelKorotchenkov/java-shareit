package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

	public static final Long ID = 1L;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemClient client;

	@SneakyThrows
	@Test
	void addItem_whenValidItemCreateDto_thenReturnOk() {
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(true).build();

		mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().is2xxSuccessful());
	}

	@SneakyThrows
	@Test
	void addItem_whenItemCreateDtoWithoutName_thenReturnBadRequest() {
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("")
				.description("description")
				.available(true).build();

		mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void addItem_whenItemCreateDtoWithoutAvailable_thenReturnBadRequest() {
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(null).build();

		mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void addItem_whenItemCreateDtoWithoutDescription_thenReturnBadRequest() {
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("")
				.available(true).build();

		mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void addComment_whenCommentEmpty_thenReturn() {
		long itemId = 1L;
		CommentRequestDto commentToAdd = CommentRequestDto.builder().text("").build();

		mockMvc.perform(post("/items/{itemId}/comment", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentToAdd)))
				.andExpect(status().isBadRequest());
	}
}