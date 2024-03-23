package ru.practicum.shareit.booking.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Booking {
	private Long id;
	private LocalDateTime start;
	private LocalDateTime end;
	private Long item;
	private Long booker;
	private Status status;
}
