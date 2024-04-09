package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@Builder
@ToString
public class BookingResponseDto {
	private long id;
	private String start;
	private String end;
	private Status status;
	private UserDto booker;
	private ItemDto item;
}
