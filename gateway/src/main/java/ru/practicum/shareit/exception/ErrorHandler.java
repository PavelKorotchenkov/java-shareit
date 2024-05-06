package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

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
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		errorResponse.setStacktrace(sw.toString());
		return errorResponse;
	}

}
