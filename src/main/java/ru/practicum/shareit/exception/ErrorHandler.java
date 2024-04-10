package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handler(final AlreadyExistsException e) {
		log.info("Ошибка - сущность уже существует.");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handler(final NotFoundException e) {
		log.info("Ошибка - сущность не найдена.");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handler(final AccessDeniedException e) {
		log.info("Ошибка - доступ запрещен.");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handler(final NotAvailableException e) {
		log.info("Ошибка - вещь недоступна для бронирования.");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handler(final BookingDateException e) {
		log.info("Ошибка - выбрано некорректное время бронирования.");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handler(final MethodArgumentNotValidException e) {
		log.info("Ошибка - передан некорректный параметр: {}", e.getMessage());
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(InvalidStateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handler(final InvalidStateException e) {
		log.info("Ошибка - передан некорректный статус: {}", e.getMessage());
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handler(final Exception e) {
		log.error("При обработке запроса возникла непредвиденная ошибка: ", e);
		ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
		errorResponse.setStacktrace(Arrays.toString(e.getStackTrace()));
		return errorResponse;
	}

}
