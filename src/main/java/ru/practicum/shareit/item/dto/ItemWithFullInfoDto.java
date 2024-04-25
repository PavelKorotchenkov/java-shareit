package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(of = "id")
public class ItemWithFullInfoDto {
	private long id;
	private String name;
	private String description;
	private Boolean available;
	private BookingShortDto lastBooking;
	private BookingShortDto nextBooking;
	private List<CommentShort> comments;
	private Long requestId;
}
