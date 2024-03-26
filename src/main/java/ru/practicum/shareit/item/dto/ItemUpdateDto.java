package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Null;

@Getter
@Setter
@Builder
@ToString
public class ItemUpdateDto {
	@Null
	private Long id;
	private String name;
	private String description;
	private Boolean available;
}
