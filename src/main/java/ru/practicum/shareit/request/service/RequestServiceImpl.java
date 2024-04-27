package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestRepository requestRepository;
	private final ItemRepository itemRepository;
	private final UserService userService;
	private final Sort byCreated = Sort.by(Sort.Direction.DESC, "Created");

	@Override
	public ItemRequestResponseDto addRequest(ItemRequestCreateDto itemRequestDto, long userId) {
		UserDto user = userService.getById(userId);
		ItemRequest itemRequest = ItemRequestDtoMapper.ofItemRequestCreateDto(itemRequestDto);
		itemRequest.setCreated(LocalDateTime.now());
		itemRequest.setRequester(UserDtoMapper.ofUserDto(user));
		return ItemRequestDtoMapper.toItemRequestResponseDto(requestRepository.save(itemRequest));
	}

	@Override
	public List<ItemRequestResponseDto> getRequests(long userId) {
		UserDto user = userService.getById(userId);
		List<ItemRequest> requests = requestRepository.findByRequesterId(user.getId(), byCreated);
		List<ItemRequestResponseDto> list = ItemRequestDtoMapper.toItemRequestResponseDto(requests);
		return setItemsForRequest(list);
	}

	@Override
	public List<ItemRequestResponseDto> getOtherRequests(long userId, Pageable pageable) {
		userService.getById(userId);
		PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()).withSort(byCreated);
		List<ItemRequest> requests = requestRepository.findByRequesterIdNot(userId, page).getContent();
		List<ItemRequestResponseDto> list = ItemRequestDtoMapper.toItemRequestResponseDto(requests);
		return setItemsForRequest(list);
	}


	@Override
	public ItemRequestResponseDto getRequestById(long userId, long requestId) {
		userService.getById(userId);
		ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден."));
		List<ItemDto> items = itemRepository.findByRequestId(requestId)
				.stream()
				.map(ItemDtoMapper::toItemDto)
				.collect(Collectors.toList());
		ItemRequestResponseDto itemRequestResponseDto = ItemRequestDtoMapper.toItemRequestResponseDto(request);
		itemRequestResponseDto.setItems(items);
		return itemRequestResponseDto;
	}

	private List<ItemRequestResponseDto> setItemsForRequest(List<ItemRequestResponseDto> requests) {
		Map<Long, List<ItemDto>> items = itemRepository
				.findByRequestIdIsNotNull()
				.stream()
				.map(ItemDtoMapper::toItemDto)
				.collect(Collectors.groupingBy(ItemDto::getRequestId));

		return requests.stream()
				.peek(request -> {
					List<ItemDto> itemList = items.getOrDefault(request.getId(), Collections.emptyList());
					request.setItems(itemList);
				})
				.collect(Collectors.toList());
	}
}
