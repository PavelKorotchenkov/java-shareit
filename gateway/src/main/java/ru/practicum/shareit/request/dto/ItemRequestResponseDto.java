package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ItemRequestResponseDto {
	private Long id;
	private String description;
	private String created;
	private List<ItemDto> items;
}
