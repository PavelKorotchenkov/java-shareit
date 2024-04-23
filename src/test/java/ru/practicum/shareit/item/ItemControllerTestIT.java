package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTestIT {
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	public static final Long ID = 1L;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemService itemService;

	@SneakyThrows
	@Test
	void addItem_whenValidItemCreateDto_thenReturnOk() {
		long userId = 1L;
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(true).build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(1L)
				.name("name")
				.description("description")
				.available(true)
				.ownerId(userId).build();

		when(itemService.add(itemCreateDtoToSave)).thenReturn(expectedResponse);

		String result = mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
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

		verify(itemService, never()).add(itemCreateDtoToSave);
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

		verify(itemService, never()).add(itemCreateDtoToSave);
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

		verify(itemService, never()).add(itemCreateDtoToSave);
	}

	@SneakyThrows
	@Test
	void updateItem_whenValidHeader_thenReturnStatusOkWithUpdatedItemInBody() {
		long itemId = 1L;
		ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.ownerId(ID).build();

		when(itemService.update(itemUpdateDto)).thenReturn(expectedResponse);

		String result = mockMvc.perform(patch("/items/{id}", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemUpdateDto)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		verify(itemService, times(1)).update(itemUpdateDto);
		assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
	}

	@SneakyThrows
	@Test
	void updateItem_whenNoHeader_thenReturnInternalServerError() {
		long itemId = 1L;
		ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.build();

		mockMvc.perform(patch("/items/{id}", itemId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemUpdateDto)))
				.andExpect(status().isInternalServerError());

		verify(itemService, never()).update(itemUpdateDto);
	}

	@SneakyThrows
	@Test
	void getItem_whenValidHeader_thenReturnStatusOkWithItemWithFullInfoDtoInBody() {
		long itemId = 1L;
		ItemWithFullInfoDto expectedItem = ItemWithFullInfoDto.builder()
				.id(itemId)
				.name("name")
				.description("description")
				.available(true)
				.lastBooking(null)
				.nextBooking(null)
				.comments(new ArrayList<>())
				.requestId(null).build();

		when(itemService.getById(itemId, ID)).thenReturn(expectedItem);

		String result = mockMvc.perform(get("/items/{id}", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(expectedItem), result);
	}

	@SneakyThrows
	@Test
	void getItem_whenNoHeader_thenReturnInternalServerError() {
		long id = 1L;
		mockMvc.perform(get("/items/{id}", id))
				.andExpect(status().isInternalServerError());

		verify(itemService, never()).getById(id, ID);
	}

	@SneakyThrows
	@Test
	void findAllByOwnerId_whenValidHeader_thenReturnStatusOkWithExpectedListInBody() {
		int from = 0;
		int size = 1;
		List<ItemWithFullInfoDto> expectedList = Collections.emptyList();

		Pageable page = PageRequest.of(from, size);
		when(itemService.getUserItems(ID, page)).thenReturn(expectedList);

		String result = mockMvc.perform(get("/items")
						.header(X_SHARER_USER_ID, ID)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(expectedList), result);
	}

	@SneakyThrows
	@Test
	void findAllByOwnerId_whenNoHeader_thenReturnInternalServerError() {
		mockMvc.perform(get("/items"))
				.andExpect(status().isInternalServerError());

		verify(itemService, never()).getUserItems(anyInt(), any());
	}

	@SneakyThrows
	@Test
	void findAllByOwnerId_whenNotValidPage_thenReturnIllegalArgumentException() {
		int from = -1;
		int size = 1;

		mockMvc.perform(get("/items")
						.header(X_SHARER_USER_ID, ID)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isInternalServerError());
		verify(itemService, never()).getUserItems(anyInt(), any());
	}

	@SneakyThrows
	@Test
	void searchBy_whenValidPage_thenReturnStatusOk() {
		int from = 0;
		int size = 1;
		String searchBy = "text";

		mockMvc.perform(get("/items/search")
						.header(X_SHARER_USER_ID, ID)
						.param("text", searchBy)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void searchBy_whenNotValidPage_thenReturnIllegalArgumentException() {
		int from = -1;
		int size = 1;
		String searchBy = "text";

		mockMvc.perform(get("/items/search")
						.header(X_SHARER_USER_ID, ID)
						.param("text", searchBy)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(result -> {
					if (!(result.getResolvedException() instanceof IllegalArgumentException)) {
						throw new AssertionError("Expected IllegalArgumentException, but got: " +
								result.getResolvedException());
					}
				});
		verify(itemService, never()).searchBy(anyLong(), anyString(), any());
	}

	@SneakyThrows
	@Test
	void addComment_whenCommentNotEmpty_thenReturnStatusOkWithCommentInBody() {
		long itemId = 1L;
		CommentRequestDto commentToAdd = CommentRequestDto.builder()
				.text("comment")
				.build();

		CommentResponseDto expectedComment = CommentResponseDto.builder()
				.id(1L)
				.text("comment")
				.authorName("author")
				.created("2024-04-15T15:30:10")
				.build();

		when(itemService.addComment(commentToAdd)).thenReturn(expectedComment);

		String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentToAdd)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(expectedComment), result);
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

		verify(itemService, never()).addComment(any());

	}
}