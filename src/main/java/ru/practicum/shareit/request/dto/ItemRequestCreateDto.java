package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class ItemRequestCreateDto {
	private long id;
	@NotEmpty(message = "У запроса должно быть описание.")
	private String description;
}
