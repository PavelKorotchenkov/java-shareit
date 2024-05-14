package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final ItemService itemService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
						   @RequestBody ItemCreateDto itemCreateDto) {
		log.info("Получен запрос на создание вещи = {}, ownerId = {}", itemCreateDto, ownerId);
		itemCreateDto.setOwnerId(ownerId);
		ItemDto response = itemService.add(itemCreateDto);
		log.info("Отработан запрос на добавление вещи: {}", response);
		return response;
	}

	@PatchMapping("/{id}")
	public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
							  @PathVariable Long id,
							  @RequestBody ItemUpdateDto itemUpdateDto) {
		log.info("Получен запрос на обновление вещи c id = {}, описание вещи = {} , ownerId = {}", itemUpdateDto, id, ownerId);
		itemUpdateDto.setId(id);
		itemUpdateDto.setOwnerId(ownerId);
		ItemDto response = itemService.update(itemUpdateDto);
		log.info("Отработан запрос на обновление вещи: {}", response);
		return response;
	}

	@GetMapping("/{id}")
	public ItemWithFullInfoDto getItem(@RequestHeader(X_SHARER_USER_ID) long userId,
									   @PathVariable Long id) {
		log.info("Получен запрос на получение вещи, item_id: {}", id);
		ItemWithFullInfoDto response = itemService.getById(id, userId);
		log.info("Отработан запрос на получение вещи, item_id: {}", id);
		return response;
	}

	@GetMapping
	public List<ItemWithFullInfoDto> findAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) long ownerId,
													  @RequestParam(required = false, defaultValue = "0") int from,
													  @RequestParam(required = false, defaultValue = "10") int size) {
		log.info("Получен запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		List<ItemWithFullInfoDto> response = itemService.findByOwnerId(ownerId, page);
		log.info("Отработан запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		return response;
	}

	@GetMapping("/search")
	public List<ItemDto> searchBy(@RequestHeader(X_SHARER_USER_ID) long userId,
								  @RequestParam String text,
								  @RequestParam(required = false, defaultValue = "0") int from,
								  @RequestParam(required = false, defaultValue = "10") int size) {
		log.info("Получен запрос на поиск всех вещей по тексту: {}", text);
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		List<ItemDto> response = itemService.searchBy(userId, text, page);
		log.info("Отработан запрос на поиск всех вещей по тексту: {}", text);
		return response;
	}

	@PostMapping("/{itemId}/comment")
	public CommentResponseDto addComment(@RequestHeader(X_SHARER_USER_ID) long userId,
										 @PathVariable Long itemId,
										 @RequestBody CommentRequestDto commentRequestDto) {
		log.info("Получен запрос на добавление комментария для вещи: {}, {}", itemId, commentRequestDto);
		commentRequestDto.setAuthorId(userId);
		commentRequestDto.setItemId(itemId);
		CommentResponseDto response = itemService.addComment(commentRequestDto);
		log.info("Отработан запрос на добавление комментария для вещи: {}, {}", itemId, response);
		return response;
	}
}
