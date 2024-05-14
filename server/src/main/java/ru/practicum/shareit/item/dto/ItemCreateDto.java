package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ItemCreateDto {
	private String name;
	private String description;
	private Boolean available;
	private long ownerId;
	private Long requestId;
}
