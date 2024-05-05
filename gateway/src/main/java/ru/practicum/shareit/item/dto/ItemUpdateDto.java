package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ItemUpdateDto {
	private long id;
	private String name;
	private String description;
	private Boolean available;
	private long ownerId;
}
