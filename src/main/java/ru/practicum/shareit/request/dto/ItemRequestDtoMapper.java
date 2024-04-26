package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestDtoMapper {

	public static ItemRequest ofItemRequestCreateDto(ItemRequestCreateDto request) {
		return ItemRequest.builder()
				.id(request.getId())
				.description(request.getDescription())
				.build();
	}

	public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request) {
		return ItemRequestResponseDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.created(request.getCreated() != null ? DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(request.getCreated()) : null)
				.build();
	}

	public static List<ItemRequestResponseDto> toItemRequestResponseDto(List<ItemRequest> requests) {
		return requests.stream()
				.map(ItemRequestDtoMapper::toItemRequestResponseDto)
				.collect(Collectors.toList());
	}

}
