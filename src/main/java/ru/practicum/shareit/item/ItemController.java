package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
	private final ItemService itemService;

	private final UserService userService;

	@Autowired
	public ItemController(ItemService itemService, UserService userService) {
		this.itemService = itemService;
		this.userService = userService;
	}

	@PostMapping
	public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
						   @Valid @RequestBody ItemDto itemDto) {
		log.info("Получен запрос на добавление вещи: {}", itemDto);
		User owner = userService.getById(userId);
		Item item = ItemDtoMapper.toItem(itemDto);
		Item savedItem = itemService.add(item, owner);
		ItemDto savedItemDto = ItemDtoMapper.toDto(savedItem);
		log.info("Отработан запрос на добавление вещи: {}", savedItemDto);
		return savedItemDto;
	}

	@PatchMapping("/{id}")
	public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
							  @PathVariable Long id,
							  @RequestBody ItemDto itemDto) {
		log.info("Получен запрос на обновление вещи: {}", itemDto);
		User owner = userService.getById(userId);
		Item item = ItemDtoMapper.toItem(itemDto);
		Item updatedItem = itemService.update(item, id, owner);
		ItemDto updatedItemDto = ItemDtoMapper.toDto(updatedItem);
		log.info("Отработан запрос на обновление вещи: {}", updatedItemDto);
		return updatedItemDto;
	}

	@GetMapping("/{id}")
	public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
						   @PathVariable Long id) {
		User owner = userService.getById(userId);
		Item item = itemService.getById(id);
		return ItemDtoMapper.toDto(item);
	}

	@GetMapping
	public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Получен запрос на получение всех вещей владельца вещи: " + userId);
		User owner = userService.getById(userId);
		List<ItemDto> items = itemService.showItemsByOwner(userId)
				.stream()
				.map(ItemDtoMapper::toDto)
				.collect(Collectors.toList());
		log.info("Отработан запрос на получение всех вещей владельца вещи: " + userId);
		return items;
	}

	@GetMapping("/search")
	public List<ItemDto> searchByText(@RequestHeader("X-Sharer-User-Id") long userId,
									  @RequestParam String text) {
		log.info("Получен запрос на поиск всех вещей по тексту: " + text);
		User owner = userService.getById(userId);
		List<ItemDto> items = itemService.searchAvailableByText(text.toLowerCase())
				.stream()
				.map(ItemDtoMapper::toDto)
				.collect(Collectors.toList());
		log.info("Отработан запрос на поиск всех вещей по тексту: " + text);
		return items;
	}
}
