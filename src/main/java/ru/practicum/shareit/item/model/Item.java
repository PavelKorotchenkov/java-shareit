package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {
	private Long id;
	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	private String description;
	@NotNull
	private Boolean available;
	private User owner;

	public Item(Long id, String name, String description, Boolean available) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.available = available;
	}
}
