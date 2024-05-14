package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final ItemRequestClient itemRequestClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
		log.info("Валидация - получен запрос на новую вещь от пользователя с usedId = {}, запрос = {}", userId, itemRequestDto);
		return itemRequestClient.addRequest(userId, itemRequestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getRequests(@RequestHeader(X_SHARER_USER_ID) long ownerId) {
		log.info("Валидация - получен запрос на получение всех собственных запросов пользователя с userId = {}", ownerId);
		return itemRequestClient.getRequests(ownerId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getOtherRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
												   @RequestParam(required = false, defaultValue = "0") int from,
												   @RequestParam(required = false, defaultValue = "10") int size) {
		log.info("Валидация - получен запрос на просмотр запросов, созданных другими пользователями, от пользователя с userId = {}, from = {}, size = {}", userId, from, size);
		return itemRequestClient.getOtherRequests(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
												 @PathVariable Long requestId) {
		log.info("Валидация - получен запрос на просмотр запроса с id = {} от пользователя с userId = {}", requestId, userId);
		return itemRequestClient.getRequestById(userId, requestId);
	}
}
