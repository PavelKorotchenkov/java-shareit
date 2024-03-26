package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
	ItemDto add(ItemCreateDto itemCreateDto, Long ownerId);

	ItemDto update(ItemUpdateDto itemUpdateDto, Long ownerId);

	ItemDto getById(Long itemId, Long userId);

	List<ItemDto> findByOwnerId(Long ownerId);

	List<ItemDto> searchBy(String text, Long userId);
}
