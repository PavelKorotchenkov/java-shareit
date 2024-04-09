package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemDtoMapper {
	public static ItemDto toDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getIsAvailable())
				.ownerId(item.getUser() != null ? item.getUser().getId() : null)
				.build();
	}

	public static ItemDto toDtoIdName(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.build();
	}

	public static ItemDto toDtoIdNameOwner(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.ownerId(item.getUser().getId())
				.build();
	}

	public static Item toItem(ItemWithBookingsDto itemDto) {
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.isAvailable(itemDto.getAvailable())
				.build();
	}

	public static Item toItemCreate(ItemCreateDto itemCreateDto) {
		return Item.builder()
				.name(itemCreateDto.getName())
				.description(itemCreateDto.getDescription())
				.isAvailable(itemCreateDto.getAvailable())
				.build();
	}

	public static ItemUpdateDto toItemUpdateDto(Item item) {
		return ItemUpdateDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getIsAvailable())
				.build();
	}

	public static ItemWithBookingsDto toItemWithBookingsDto(Item item) {
		return ItemWithBookingsDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getIsAvailable())
				.ownerId(item.getUser().getId())
				.nextBooking(null)
				.lastBooking(null)
				.build();
	}
}
