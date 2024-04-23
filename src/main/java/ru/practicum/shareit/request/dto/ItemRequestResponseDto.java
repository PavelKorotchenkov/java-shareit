package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ItemRequestResponseDto {
	private Long id;
	@NotEmpty
	private String description;
	private String created;
	private List<ItemForRequestDto> items;
}
