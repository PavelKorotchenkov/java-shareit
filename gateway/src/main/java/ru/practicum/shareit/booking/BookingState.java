package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
	WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE, ALL;

	public static Optional<BookingState> getState(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
