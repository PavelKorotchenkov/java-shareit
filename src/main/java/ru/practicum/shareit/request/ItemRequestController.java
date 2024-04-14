package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

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
	public ItemRequestResponseDto addRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
		log.info("Получен запрос на новый item request: {}", itemRequestDto);
		ItemRequestResponseDto itemRequestSaved = requestService.addRequest(itemRequestDto, userId);
		log.info("Отработан запрос на новый item request: {}", itemRequestSaved);
		return itemRequestSaved;
	}

	@GetMapping
	public List<ItemRequestResponseDto> getRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
		log.info("Получен запрос на все item request для пользователя с id: {}", userId);
		List<ItemRequestResponseDto> response = requestService.getRequests(userId);
		log.info("Обработан запрос на все item request: {}", response);
		return response;
	}

	@GetMapping("/all")
	public List<ItemRequestResponseDto> getOtherRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
														 @RequestParam(required = false) Integer from,
														 @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на все item request для пользователя с id: {}", userId);
		List<ItemRequestResponseDto> response;
		if (from == null || size == null) {
			response = requestService.getOtherRequests(userId);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			response = requestService.getOtherRequestsPageable(userId, from, size);
		}
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
