package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.InvalidStateException;

public enum State {
	WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE, ALL;

	public static State getState(String state) {
		for (State s : State.values()) {
			if (s.name().equalsIgnoreCase(state)) {
				return s;
			}
		}
		throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
	}
}
