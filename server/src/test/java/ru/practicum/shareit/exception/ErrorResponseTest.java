package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

	@Test
	void testErrorResponseCreation() {
		String errorMessage = "Test error message";
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
		assertEquals(errorMessage, errorResponse.getError());
	}
}