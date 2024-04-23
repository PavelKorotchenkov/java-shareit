package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private RequestServiceImpl requestService;

	@Test
	void addRequest_whenInvoked_thenReturnItemRequestResponseDto() {
		long userId = 1L;
		ItemRequestCreateDto itemRequestToSaveDto = ItemRequestCreateDto.builder().description("desc").build();
		ItemRequest expectedItemRequest = new ItemRequest();
		expectedItemRequest.setDescription("desc");

		when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).build());
		when(requestRepository.save(ItemRequestDtoMapper.toItemRequest(itemRequestToSaveDto))).thenReturn(expectedItemRequest);

		ItemRequestResponseDto actualItemRequest = requestService.addRequest(itemRequestToSaveDto, userId);

		assertEquals(expectedItemRequest.getDescription(), actualItemRequest.getDescription());
	}

	@Test
	void getRequests_whenOneItemRequestWithDescription_thenReturnListWithOneItemRequestWithDescription() {
		long userId = 0L;
		List<ItemRequest> expectedRequests = List.of(new ItemRequest());
		expectedRequests.get(0).setDescription("description");
		when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).build());
		when(requestRepository.findByUserId(eq(userId), any(Sort.class))).thenReturn(expectedRequests);

		List<ItemRequestResponseDto> actualRequests = requestService.getRequests(userId);

		assertEquals(expectedRequests.size(), actualRequests.size());
		assertEquals(expectedRequests.get(0).getDescription(), actualRequests.get(0).getDescription());
	}

	@Test
	void getOtherRequests_whenOneItemRequestWithDescription_thenReturnListWithOneItemRequestWithDescription() {
		long userId = 0L;
		List<ItemRequest> expectedRequests = List.of(new ItemRequest());
		expectedRequests.get(0).setDescription("description");
		Page<ItemRequest> page = new PageImpl<>(expectedRequests);

		when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).build());
		when(requestRepository.findByUserIdNot(eq(userId), any(PageRequest.class))).thenReturn(page);

		List<ItemRequestResponseDto> actualRequests = requestService.getOtherRequests(userId, PageRequest.of(0, 10));

		assertEquals(expectedRequests.size(), actualRequests.size());
		assertEquals(expectedRequests.get(0).getDescription(), actualRequests.get(0).getDescription());
	}

	@Test
	void getRequestById_whenFound_thenReturnItemRequest() {
		long userId = 0L;
		long requestId = 0L;
		ItemRequest expectedItemRequest = new ItemRequest();
		expectedItemRequest.setDescription("description");
		when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).build());
		when(requestRepository.findById(requestId)).thenReturn(Optional.of(expectedItemRequest));
		ItemRequestResponseDto actualRequest = requestService.getRequestById(userId, requestId);

		assertEquals(expectedItemRequest.getDescription(), actualRequest.getDescription());
	}

	@Test
	void getRequestById_whenNotFound_thenReturnNotFoundException() {
		long userId = 0L;
		long requestId = 0L;

		when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).build());
		when(requestRepository.findById(requestId)).thenThrow(NotFoundException.class);

		assertThrows(NotFoundException.class,
				() -> requestService.getRequestById(userId, requestId));
	}
}