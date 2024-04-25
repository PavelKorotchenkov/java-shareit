package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(of = "id")
public class ItemDto {
	private long id;
	private String name;
	private String description;
	private Boolean available;
	private Long requestId;
}
