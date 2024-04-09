package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemWithBookingsDto {
	private long id;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	@NotNull
	private Boolean available;
	private Long ownerId;
	private BookingShortDto lastBooking;
	private BookingShortDto nextBooking;
	private List<CommentResponseDto> comments;
}
