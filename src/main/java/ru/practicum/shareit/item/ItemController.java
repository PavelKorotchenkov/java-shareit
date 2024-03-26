package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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
		ItemDto savedItemDto = itemService.add(itemCreateDto, ownerId);
		log.info("Отработан запрос на добавление вещи: {}", savedItemDto);
		return savedItemDto;
	}

	@PatchMapping("/{id}")
	public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
							  @PathVariable Long id,
							  @RequestBody ItemUpdateDto itemUpdateDto) {
		log.info("Получен запрос на обновление вещи: {}", itemUpdateDto);
		itemUpdateDto.setId(id);
		ItemDto updatedItemDto = itemService.update(itemUpdateDto, ownerId);
		log.info("Отработан запрос на обновление вещи: {}", updatedItemDto);
		return updatedItemDto;
	}

	@GetMapping("/{id}")
	public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
						   @PathVariable Long id) {
		return itemService.getById(id, userId);
	}

	@GetMapping
	public List<ItemDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
		log.info("Получен запрос на получение всех вещей владельца вещи: " + ownerId);
		List<ItemDto> items = itemService.findByOwnerId(ownerId);
		log.info("Отработан запрос на получение всех вещей владельца вещи: " + ownerId);
		return items;
	}

	@GetMapping("/search")
	public List<ItemDto> searchBy(@RequestHeader("X-Sharer-User-Id") long userId,
								  @RequestParam String text) {
		log.info("Получен запрос на поиск всех вещей по тексту: " + text);
		List<ItemDto> items = itemService.searchBy(text.toLowerCase(), userId);
		log.info("Отработан запрос на поиск всех вещей по тексту: " + text);
		return items;
	}
}
