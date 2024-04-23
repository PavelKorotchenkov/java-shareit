package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final ItemService itemService;

	@PostMapping
	public ResponseEntity<ItemDto> addItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
										   @Valid @RequestBody ItemCreateDto itemCreateDto) {
		log.info("Получен запрос на добавление вещи: {}", itemCreateDto);
		itemCreateDto.setOwnerId(ownerId);
		ItemDto savedItemDto = itemService.add(itemCreateDto);
		log.info("Отработан запрос на добавление вещи: {}", savedItemDto);
		return ResponseEntity.ok(savedItemDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ItemDto> updateItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
											  @PathVariable Long id,
											  @RequestBody ItemUpdateDto itemUpdateDto) {
		log.info("Получен запрос на обновление вещи: {}", itemUpdateDto);
		itemUpdateDto.setId(id);
		itemUpdateDto.setOwnerId(ownerId);
		ItemDto updatedItemDto = itemService.update(itemUpdateDto);
		log.info("Отработан запрос на обновление вещи: {}", updatedItemDto);
		return ResponseEntity.ok(updatedItemDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ItemWithFullInfoDto> getItem(@RequestHeader(X_SHARER_USER_ID) long userId,
													   @PathVariable Long id) {
		log.info("Получен запрос на получение вещи, item_id: {}", id);
		ItemWithFullInfoDto byId = itemService.getById(id, userId);
		log.info("Отработан запрос на получение вещи, item_id: {}", id);
		return ResponseEntity.ok(byId);
	}

	@GetMapping
	public ResponseEntity<List<ItemWithFullInfoDto>> findAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) long ownerId,
																	  @RequestParam(required = false) Integer from,
																	  @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		PageRequest page = createPageRequest(from, size);
		List<ItemWithFullInfoDto> response = itemService.getUserItems(ownerId, page);
		log.info("Отработан запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	public ResponseEntity<List<ItemDto>> searchBy(@RequestHeader(X_SHARER_USER_ID) long userId,
												  @RequestParam String text,
												  @RequestParam(required = false) Integer from,
												  @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на поиск всех вещей по тексту: {}", text);
		PageRequest page = createPageRequest(from, size);
		List<ItemDto> response = itemService.searchBy(userId, text, page);
		log.info("Отработан запрос на поиск всех вещей по тексту: {}", text);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<CommentResponseDto> addComment(@RequestHeader(X_SHARER_USER_ID) long userId,
														 @PathVariable Long itemId,
														 @Valid @RequestBody CommentRequestDto commentRequestDto) {
		log.info("Получен запрос на добавление комментария для вещи: {}, {}", itemId, commentRequestDto);
		commentRequestDto.setAuthorId(userId);
		commentRequestDto.setItemId(itemId);
		CommentResponseDto responseDto = itemService.addComment(commentRequestDto);
		log.info("Отработан запрос на добавление комментария для вещи: {}, {}", itemId, responseDto);
		return ResponseEntity.ok(responseDto);
	}

	private PageRequest createPageRequest(Integer from, Integer size) {
		PageRequest page;
		if (from == null || size == null) {
			page = PageRequest.of(0, Integer.MAX_VALUE);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			page = PageRequest.of(from > 0 ? from / size : 0, size);
		}
		return page;
	}
}
