package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
public class BookingRequestDto {
	private String start;
	private String end;
	@NotNull
	private long itemId;
	private long bookerId;
}
