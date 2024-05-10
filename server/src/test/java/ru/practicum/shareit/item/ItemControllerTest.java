package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
	private ItemService itemService;

	@Test
	void addItem_whenValidItemCreateDto_thenReturnOk() throws JsonProcessingException, Exception {
		ItemCreateDto itemCreateDtoToSave = ItemCreateDto.builder()
				.name("name")
				.description("description")
				.available(true).build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(1L)
				.name("name")
				.description("description")
				.available(true)
				.build();

		when(itemService.add(any())).thenReturn(expectedResponse);

		mockMvc.perform(post("/items")
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemCreateDtoToSave)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("name"))
				.andExpect(jsonPath("$.description").value("description"));
	}

	@Test
	void updateItem_whenValidHeader_thenReturnStatusOkWithUpdatedItemInBody() throws JsonProcessingException, Exception {
		long itemId = 1L;

		ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.ownerId(1L)
				.build();

		ItemDto expectedResponse = ItemDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.build();

		when(itemService.update(any())).thenReturn(expectedResponse);

		mockMvc.perform(patch("/items/{id}", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemUpdateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("name upd"))
				.andExpect(jsonPath("$.description").value("description upd"))
				.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	void updateItem_whenNoHeader_thenReturnInternalServerError() throws JsonProcessingException, Exception {
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

	@Test
	void updateItem_whenUserIsNotOwner_thenReturnAccessDeniedException() throws JsonProcessingException, Exception {
		long itemId = 1L;
		ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
				.id(itemId)
				.name("name upd")
				.description("description upd")
				.available(true)
				.build();

		when(itemService.update(any())).thenThrow(AccessDeniedException.class);

		mockMvc.perform(patch("/items/{id}", itemId)
						.header(X_SHARER_USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemUpdateDto)))
				.andExpect(status().isForbidden());
	}

	@Test
	void getItem_whenValidHeader_thenReturnStatusOkWithItemWithFullInfoDtoInBody() throws JsonProcessingException, Exception {
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

		mockMvc.perform(get("/items/{id}", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("name"))
				.andExpect(jsonPath("$.description").value("description"));
	}

	@Test
	void getItem_whenNoHeader_thenReturnInternalServerError() throws Exception {
		long id = 1L;
		mockMvc.perform(get("/items/{id}", id))
				.andExpect(status().isInternalServerError());

		verify(itemService, never()).getById(id, ID);
	}

	@Test
	void findAllByOwnerId_whenValidHeader_thenReturnStatusOkWithExpectedListInBody() throws JsonProcessingException, Exception {
		int from = 0;
		int size = 1;
		List<ItemWithFullInfoDto> expectedList = List.of(ItemWithFullInfoDto.builder().id(1L).name("name").build());

		Pageable page = PageRequest.of(from, size);
		when(itemService.findByOwnerId(ID, page)).thenReturn(expectedList);

		mockMvc.perform(get("/items")
						.header(X_SHARER_USER_ID, ID)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("name"));
	}

	@Test
	void findAllByOwnerId_whenNoHeader_thenReturnInternalServerError() throws Exception {
		mockMvc.perform(get("/items"))
				.andExpect(status().isInternalServerError());

		verify(itemService, never()).findByOwnerId(anyInt(), any());
	}

	@Test
	void findAllByOwnerId_whenNotValidPage_thenReturnIllegalArgumentException() throws Exception {
		int from = -1;
		int size = 1;

		mockMvc.perform(get("/items")
						.header(X_SHARER_USER_ID, ID)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isInternalServerError());
		verify(itemService, never()).findByOwnerId(anyInt(), any());
	}

	@Test
	void searchBy_whenValidPage_thenReturnStatusOk() throws Exception {
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

	@Test
	void searchBy_whenNotValidPage_thenReturnIllegalArgumentException() throws Exception {
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

	@Test
	void addComment_whenCommentNotEmpty_thenReturnStatusOkWithCommentInBody() throws JsonProcessingException, Exception {
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

		when(itemService.addComment(any())).thenReturn(expectedComment);

		mockMvc.perform(post("/items/{itemId}/comment", itemId)
						.header(X_SHARER_USER_ID, ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentToAdd)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.text").value("comment"))
				.andExpect(jsonPath("$.authorName").value("author"))
				.andExpect(jsonPath("$.created").value("2024-04-15T15:30:10"));
	}
}