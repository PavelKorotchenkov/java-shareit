package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	private final String error;
	private String stacktrace;

	public ErrorResponse(String error) {
		this.error = error;
	}
}
