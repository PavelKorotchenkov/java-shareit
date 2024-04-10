package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemResponseDto {
	private long id;

	private String name;

	private String description;

	private Boolean available;
	private BookingShortDto lastBooking;
	private BookingShortDto nextBooking;
	private List<CommentShort> comments;
}
