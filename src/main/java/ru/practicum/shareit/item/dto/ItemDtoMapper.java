package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemDtoMapper {
	public static ItemDto toDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
				.requestId(item.getRequest() != null ? item.getRequest().getId() : null)
				.build();
	}

	public static Item toItem(ItemDto itemDto) {
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.owner(itemDto.getOwnerId() != null ? new User() : null)
				.request(itemDto.getRequestId() != null ? new ItemRequest() : null)
				.build();
	}
}
