package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.InvalidStateException;

public enum BookingState {
	WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE, ALL;

	public static BookingState getState(String state) {
		for (BookingState s : BookingState.values()) {
			if (s.name().equalsIgnoreCase(state)) {
				return s;
			}
		}
		throw new InvalidStateException("Unknown state: " + state);
	}
}
