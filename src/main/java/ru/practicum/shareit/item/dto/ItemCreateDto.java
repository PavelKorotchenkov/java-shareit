package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(of = "name")
public class ItemCreateDto {
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	@NotNull
	private Boolean available;
	private long ownerId;
	private Long requestId;
}
