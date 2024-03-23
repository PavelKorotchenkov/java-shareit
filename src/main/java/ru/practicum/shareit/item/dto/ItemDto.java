package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ItemDto {
	private Long id;
	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	private String description;
	@NotNull
	private Boolean available;
	private Long ownerId;
	private Long requestId;
}
