package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class BookingCreateDto {
	private String start;
	private String end;
	@NotNull
	private long itemId;
	private long bookerId;
}
