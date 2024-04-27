package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
@ToString
public class ItemRequestCreateDto {
	private long id;
	@NotEmpty(message = "У запроса должно быть описание.")
	private String description;
}
