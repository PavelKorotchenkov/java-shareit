package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RequestService requestService;

	@Test
	void getRequests_whenInvoked_thenReturnStatusOkWithEmptyListInBody() throws Exception {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = List.of(ItemRequestResponseDto.builder().id(1L).description("desc").build());
		when(requestService.getRequests(anyLong())).thenReturn(expectedList);
		mockMvc.perform(get("/requests")
						.header(X_SHARER_USER_ID, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].description").value("desc"));
	}

	@Test
	void getOtherRequests_whenPageParamsValid_thenReturnStatusOkWithEmptyListInBody() throws Exception {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = List.of(ItemRequestResponseDto.builder()
				.id(1L).description("desc").build());
		when(requestService.getOtherRequests(anyLong(), any())).thenReturn(expectedList);
		mockMvc.perform(get("/requests/all")
						.header(X_SHARER_USER_ID, userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].description").value("desc"));
	}

	@Test
	void getOtherRequests_whenPageParamsNotValid_thenReturnInternalServerError() throws Exception {
		long userId = 1L;
		int from = -1;
		int size = 2;

		mockMvc.perform(get("/requests/all")
						.header(X_SHARER_USER_ID, userId)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void getRequestById_whenInvokedWithCorrectId_thenReturnItemRequestResponseDto() throws Exception {
		long userId = 1L;
		long requestId = 1L;
		List<ItemDto> list = Collections.emptyList();
		ItemRequestResponseDto expectedItemRequest = ItemRequestResponseDto.builder()
				.id(1L)
				.description("description")
				.created("2024-01-01T10:10:10")
				.items(list).build();

		when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(expectedItemRequest);

		mockMvc.perform(get("/requests/{requestId}", requestId)
						.header(X_SHARER_USER_ID, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(expectedItemRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.description").value("description"))
				.andExpect(jsonPath("$.created").value("2024-01-01T10:10:10"))
				.andExpect(jsonPath("$.items").isEmpty());
	}
}