package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTestIT {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RequestService requestService;


	@SneakyThrows
	@Test
	void addRequest_whenValidDescription_thenAddNewRequest() {
		List<ItemForRequestDto> list = Collections.emptyList();
		ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
				.description("description").build();
		ItemRequestResponseDto expectedRequest = ItemRequestResponseDto.builder()
				.id(1L)
				.description("description")
				.created("2024-01-01T10:10:10")
				.items(list).build();

		when(requestService.addRequest(itemRequestCreateDto, 1L)).thenReturn(expectedRequest);

		String result = mockMvc.perform(post("/requests")
						.header(X_SHARER_USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequestCreateDto)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertEquals(objectMapper.writeValueAsString(expectedRequest), result);
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

	@SneakyThrows
	@Test
	void getRequests_whenInvoked_thenReturnStatusOkWithEmptyListInBody() {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = Collections.emptyList();
		when(requestService.getRequests(userId)).thenReturn(expectedList);
		String result = mockMvc.perform(get("/requests")
						.header(X_SHARER_USER_ID, userId)
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
	void getOtherRequests_whenPageParamsValid_thenReturnStatusOkWithEmptyListInBody() {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = Collections.emptyList();
		when(requestService.getOtherRequests(userId, PageRequest.of(1, 1))).thenReturn(expectedList);
		String result = mockMvc.perform(get("/requests")
						.header(X_SHARER_USER_ID, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(expectedList)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertEquals(objectMapper.writeValueAsString(expectedList), result);
	}

	@SneakyThrows
	@Test
	void getOtherRequests_whenPageParamsNotValid_thenReturnInternalServerError() {
		long userId = 1L;
		int from = -1;
		int size = 2;

		mockMvc.perform(get("/requests/all")
						.header(X_SHARER_USER_ID, userId)
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isInternalServerError());
	}

	@SneakyThrows
	@Test
	void getRequestById_whenInvokedWithCorrectId_thenReturnIRRDto() {
		long userId = 1L;
		long requestId = 1L;
		List<ItemForRequestDto> list = Collections.emptyList();
		ItemRequestResponseDto expectedItemRequest = ItemRequestResponseDto.builder()
				.id(1L)
				.description("description")
				.created("2024-01-01T10:10:10")
				.items(list).build();

		when(requestService.getRequestById(userId, requestId)).thenReturn(expectedItemRequest);

		String result = mockMvc.perform(get("/requests/{requestId}", requestId)
						.header(X_SHARER_USER_ID, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(expectedItemRequest)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(expectedItemRequest), result);
	}
}