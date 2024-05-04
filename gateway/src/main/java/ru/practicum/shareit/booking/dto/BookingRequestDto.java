package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

@Getter
@Setter
@Builder
@ToString
public class BookingRequestDto {
	@FutureOrPresent
	private String start;
	@Future
	private String end;
	private long itemId;
}
