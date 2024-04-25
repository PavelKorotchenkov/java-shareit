package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
	@Mock
	private RequestService requestService;

	@InjectMocks
	private ItemRequestController itemRequestController;

	@Test
	void addRequest_whenInvoked_thenReturnStatusOkWithResponseDtoInBody() {
		long userId = 1L;
		ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
				.description("description").build();

		when(requestService.addRequest(itemRequestCreateDto, userId)).thenReturn(ItemRequestResponseDto.builder()
				.description("description").build());

		ItemRequestResponseDto response = itemRequestController.addRequest(userId, itemRequestCreateDto);

		assertEquals(itemRequestCreateDto.getDescription(), Objects.requireNonNull(response).getDescription());
	}

	@Test
	void getRequests_whenInvoked_thenReturnStatusOkWithListOfResponseDtoInBody() {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = List.of(ItemRequestResponseDto.builder().build());
		when(requestService.getRequests(userId)).thenReturn(expectedList);
		List<ItemRequestResponseDto> response = itemRequestController.getRequests(userId);

		assertEquals(expectedList, response);
	}

	@Test
	void getOtherRequests_whenPageParamsValid_thenReturnStatusOkWithListOfResponseDtoInBody() {
		long userId = 1L;
		List<ItemRequestResponseDto> expectedList = List.of(ItemRequestResponseDto.builder().build());
		when(requestService.getOtherRequests(userId, PageRequest.of(1, 1))).thenReturn(expectedList);
		List<ItemRequestResponseDto> response = itemRequestController.getOtherRequests(userId, 1, 1);
		assertEquals(expectedList, response);
	}

	@Test
	void getRequestById_whenInvoke_thenReturnStatusOkWithResponseDtoInBody() {
		long userId = 1L;
		long requestId = 1L;
		ItemRequestResponseDto expectedRequest = ItemRequestResponseDto.builder().build();
		when(requestService.getRequestById(userId, requestId)).thenReturn(expectedRequest);
		ItemRequestResponseDto response = itemRequestController.getRequestById(userId, requestId);

		assertEquals(expectedRequest, response);
	}
}