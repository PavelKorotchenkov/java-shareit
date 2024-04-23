package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
	public static ItemDto toDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.ownerId(item.getUser() != null ? item.getUser().getId() : null)
				.requestId(item.getRequest() == null? null : item.getRequest().getId())
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
				.requestId(item.getRequest() == null ? null : item.getRequest().getId())
				.build();
	}

	public static Item toItem(ItemWithFullInfoDto itemDto) {
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.build();
	}

	public static Item toItemCreate(ItemCreateDto itemCreateDto) {
		return Item.builder()
				.name(itemCreateDto.getName())
				.description(itemCreateDto.getDescription())
				.available(itemCreateDto.getAvailable())
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

	public static ItemWithFullInfoDto toItemWithBookingsDto(Item item) {
		return ItemWithFullInfoDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.nextBooking(null)
				.lastBooking(null)
				.requestId(item.getRequest() == null? null : item.getRequest().getId())
				.build();
	}

	public static ItemForRequestDto toItemForRequestDto(Item item) {
		return ItemForRequestDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.requestId(item.getRequest().getId())
				.build();
	}
}
