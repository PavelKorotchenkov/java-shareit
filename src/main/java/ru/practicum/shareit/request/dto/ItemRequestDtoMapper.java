package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestDtoMapper {


	public static ItemRequest toItemRequest(ItemRequestCreateDto request) {
		return ItemRequest.builder()
				.id(request.getId())
				.description(request.getDescription())
				.build();
	}

	public static ItemRequestResponseDto toResponseDto(ItemRequest request) {
		return ItemRequestResponseDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.created(request.getCreated() != null ? DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(request.getCreated()) : null)
				.items(mapItemsForRequestDto(request.getItems()))
				.build();
	}

	private static List<ItemForRequestDto> mapItemsForRequestDto(List<Item> items) {
		List<ItemForRequestDto> dtoList = null;
		if (items != null) {
			dtoList = items.stream()
					.map(ItemDtoMapper::toItemForRequestDto)
					.collect(Collectors.toList());
		}
		return dtoList;
	}
}
