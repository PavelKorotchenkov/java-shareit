package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	private final ItemService itemService;

	@PostMapping
	public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
						   @Valid @RequestBody ItemCreateDto itemCreateDto) {
		log.info("Получен запрос на добавление вещи: {}", itemCreateDto);
		itemCreateDto.setOwnerId(ownerId);
		ItemDto savedItemDto = itemService.add(itemCreateDto);
		log.info("Отработан запрос на добавление вещи: {}", savedItemDto);
		return savedItemDto;
	}

	@PatchMapping("/{id}")
	public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
							  @PathVariable Long id,
							  @RequestBody ItemUpdateDto itemUpdateDto) {
		log.info("Получен запрос на обновление вещи: {}", itemUpdateDto);
		itemUpdateDto.setId(id);
		itemUpdateDto.setOwnerId(ownerId);
		ItemDto updatedItemDto = itemService.update(itemUpdateDto);
		log.info("Отработан запрос на обновление вещи: {}", updatedItemDto);
		return updatedItemDto;
	}

	@GetMapping("/{id}")
	public ItemWithFullInfoDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
									   @PathVariable Long id) {
		log.info("Получен запрос на получение вещи, item_id: {}", id);
		ItemWithFullInfoDto byId = itemService.getById(id, userId);
		log.info("Отработан запрос на получение вещи, item_id: {}", id);
		return byId;
	}

	@GetMapping
	public List<ItemWithFullInfoDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
		log.info("Получен запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		List<ItemWithFullInfoDto> items = itemService.getAllByUserId(ownerId);
		log.info("Отработан запрос на получение всех вещей владельца вещи, ownerId: {}", ownerId);
		return items;
	}

	@GetMapping("/search")
	public List<ItemDto> searchBy(@RequestHeader("X-Sharer-User-Id") long userId,
								  @RequestParam String text) {
		log.info("Получен запрос на поиск всех вещей по тексту: {}", text);
		List<ItemDto> items = itemService.searchBy(text, userId);
		log.info("Отработан запрос на поиск всех вещей по тексту: {}", text);
		return items;
	}

	@PostMapping("/{itemId}/comment")
	public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
										 @PathVariable Long itemId,
										 @Valid @RequestBody CommentRequestDto commentRequestDto) {
		log.info("Получен запрос на добавление комментария для вещи: {}, {}", itemId, commentRequestDto);
		commentRequestDto.setAuthorId(userId);
		commentRequestDto.setItemId(itemId);
		CommentResponseDto responseDto = itemService.addComment(commentRequestDto);
		log.info("Отработан запрос на добавление комментария для вещи: {}, {}", itemId, responseDto);
		return responseDto;
	}
}
