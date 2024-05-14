package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final ItemClient itemClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
										  @Valid @RequestBody ItemCreateDto itemCreateDto) {
		log.info("Валидация - получен запрос на добавление вещи = {}, ownerId = {}", itemCreateDto, ownerId);
		return itemClient.addItem(ownerId, itemCreateDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
											 @PathVariable Long id,
											 @RequestBody ItemUpdateDto itemUpdateDto) {
		log.info("Валидация - получен запрос на обновление вещи c id = {}, описание вещи = {} , ownerId = {}", itemUpdateDto, id, ownerId);
		return itemClient.updateItem(id, ownerId, itemUpdateDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getItem(@RequestHeader(X_SHARER_USER_ID) long userId,
										  @PathVariable Long id) {
		log.info("Валидация - получен запрос на получение вещи, item_id = {}, userId = {}", id, userId);
		return itemClient.getItem(userId, id);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) long ownerId,
												   @RequestParam(required = false, defaultValue = "0") int from,
												   @RequestParam(required = false, defaultValue = "10") int size) {
		log.info("Валидация - получен запрос на получение всех вещей владельца вещи, ownerId = {}, from = {}, size = {}", ownerId, from, size);
		return itemClient.findAllByOwnerId(ownerId, from, size);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchBy(@RequestHeader(X_SHARER_USER_ID) long userId,
										   @RequestParam String text,
										   @RequestParam(required = false, defaultValue = "0") int from,
										   @RequestParam(required = false, defaultValue = "10") int size) {
		log.info("Валидация - получен запрос на поиск всех вещей по тексту = {}, userId = {}, from = {}, size = {}", text, userId, from, size);
		return itemClient.searchBy(userId, text, from, size);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @PathVariable Long itemId,
											 @Valid @RequestBody CommentRequestDto commentRequestDto) {
		log.info("Валидация - получен запрос на добавление комментария для вещи itemId = {}, userId = {}, комментарий = {}", itemId, userId, commentRequestDto);
		return itemClient.addComment(userId, itemId, commentRequestDto);
	}
}