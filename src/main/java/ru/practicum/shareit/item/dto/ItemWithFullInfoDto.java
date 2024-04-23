package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(of = "id")
public class ItemWithFullInfoDto {
	private long id;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	@NotNull
	private Boolean available;
	private BookingShortDto lastBooking;
	private BookingShortDto nextBooking;
	private List<CommentShort> comments;
	private Long requestId;
}
