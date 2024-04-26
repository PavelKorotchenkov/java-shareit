package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.OffsetPageRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

	private final RequestService requestService;
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemRequestResponseDto addRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
		log.info("Получен запрос на новый item request: {}", itemRequestDto);
		ItemRequestResponseDto response = requestService.addRequest(itemRequestDto, userId);
		log.info("Отработан запрос на новый item request: {}", response);
		return response;
	}

	@GetMapping
	public List<ItemRequestResponseDto> getRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
		log.info("Получен запрос на все запросы пользователя с id: {}", userId);
		List<ItemRequestResponseDto> response = requestService.getRequests(userId);
		log.info("Обработан запрос на все запросы пользователя: {}", response);
		return response;
	}

	@GetMapping("/all")
	public List<ItemRequestResponseDto> getOtherRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
														 @RequestParam(required = false) Integer from,
														 @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на просмотр запросов, созданных другими пользователями");
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		List<ItemRequestResponseDto> response = requestService.getOtherRequests(userId, page);
		log.info("Обработан запрос на все item request: {}", response);
		return response;
	}

	@GetMapping("/{requestId}")
	public ItemRequestResponseDto getRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
												 @PathVariable Long requestId) {
		log.info("Получен запрос на item request для запроса с id: {}", requestId);
		ItemRequestResponseDto response = requestService.getRequestById(userId, requestId);
		log.info("Отработан запрос на item request для запроса: {}", response);
		return response;
	}
}
