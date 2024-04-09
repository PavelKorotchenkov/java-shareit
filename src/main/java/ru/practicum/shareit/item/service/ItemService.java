package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
	ItemDto add(ItemCreateDto itemCreateDto);

	ItemDto update(ItemUpdateDto itemUpdateDto);

	ItemWithBookingsDto getById(Long itemId, long userId);

	List<ItemWithBookingsDto> findByUserId(long ownerId);

	List<ItemDto> searchBy(String text, long userId);
}
