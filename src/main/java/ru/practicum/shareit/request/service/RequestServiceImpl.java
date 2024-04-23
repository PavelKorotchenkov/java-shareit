package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestRepository requestRepository;
	private final UserService userService;
	private final Sort byCreated = Sort.by(Sort.Direction.DESC, "Created");

	@Override
	public ItemRequestResponseDto addRequest(ItemRequestCreateDto itemRequestDto, long userId) {
		UserDto user = userService.getById(userId);
		ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDto);
		itemRequest.setCreated(LocalDateTime.now());
		itemRequest.setUser(UserDtoMapper.toUser(user));
		return ItemRequestDtoMapper.toResponseDto(requestRepository.save(itemRequest));
	}

	@Override
	public List<ItemRequestResponseDto> getRequests(long userId) {
		UserDto user = userService.getById(userId);
		List<ItemRequest> requests = requestRepository.findByUserId(user.getId(), byCreated);

		return requests.stream()
				.map(ItemRequestDtoMapper::toResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemRequestResponseDto> getOtherRequests(long userId, Pageable pageable) {
		UserDto user = userService.getById(userId);
		PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()).withSort(byCreated);

		List<ItemRequest> requests = requestRepository.findByUserIdNot(userId, page).getContent();

		return requests.stream()
				.map(ItemRequestDtoMapper::toResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public ItemRequestResponseDto getRequestById(long userId, long requestId) {
		UserDto user = userService.getById(userId);
		ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден."));
		return ItemRequestDtoMapper.toResponseDto(request);
	}
}
