package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
public class ItemCreateDto {
	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	private String description;
	@NotNull
	private Boolean available;
	private Long owner;
}
