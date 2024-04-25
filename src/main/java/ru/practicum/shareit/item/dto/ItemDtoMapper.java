package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {

	public static Item ofItemWithFullInfoDto(ItemWithFullInfoDto itemDto) {
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.build();
	}

	public static Item ofItemCreateDto(ItemCreateDto itemCreateDto) {
		return Item.builder()
				.name(itemCreateDto.getName())
				.description(itemCreateDto.getDescription())
				.available(itemCreateDto.getAvailable())
				.build();
	}

	public static ItemDto toItemDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.requestId(item.getRequest() == null ? null : item.getRequest().getId())
				.build();
	}

	public static ItemUpdateDto toItemUpdateDto(Item item) {
		return ItemUpdateDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.build();
	}

	public static ItemWithFullInfoDto toItemWithFullInfoDto(Item item) {
		return ItemWithFullInfoDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.nextBooking(null)
				.lastBooking(null)
				.requestId(item.getRequest() == null ? null : item.getRequest().getId())
				.build();
	}
}
