package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidStateException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final BookingService bookingService;

	@PostMapping
	public BookingResponseDto addBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
										 @Valid @RequestBody BookingRequestDto bookingRequestDto) {
		log.info("Получен запрос на бронирование вещи: {}", bookingRequestDto);
		bookingRequestDto.setBookerId(userId);
		BookingResponseDto savedBooking = bookingService.add(bookingRequestDto);
		log.info("Обработан запрос на бронирование вещи: {}", savedBooking);
		return savedBooking;
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto approve(@RequestHeader(X_SHARER_USER_ID) long userId,
									  @PathVariable long bookingId,
									  @RequestParam boolean approved) {
		log.info("Получен запрос - решение владельца вещи по одобрению бронирования: " +
				"user id - {}, booking id - {}, approved - {}", userId, bookingId, approved);
		BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
		log.info("Обработан запрос - решение владельца вещи по одобрению бронирования: {}", bookingResponseDto);
		return bookingResponseDto;
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto getBookingInfoById(@RequestHeader(X_SHARER_USER_ID) long userId,
												 @PathVariable long bookingId) {
		log.info("Получен запрос на получение информации о бронировании: {}", bookingId);
		BookingResponseDto bookingResponseDto = bookingService.getInfoById(userId, bookingId);
		log.info("Обработан запрос на получение информации о бронировании: {}", bookingId);
		return bookingResponseDto;
	}

	@GetMapping
	public List<BookingResponseDto> getAllBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
												   @RequestParam(defaultValue = "ALL") String state,
												   @RequestParam(required = false) Integer from,
												   @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на получение всех бронирований пользователя: user id: {}, state: {}, from: {}, size: {}", userId, state, from, size);
		State validState = getState(state);
		List<BookingResponseDto> allBookings;
		if (from == null || size == null) {
			allBookings = bookingService.getAllBookings(userId, validState);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			allBookings = bookingService.getAllBookingsPageable(userId, from, size);
		}

		log.info("Обработан запрос на получение всех бронирований пользователя: {}", allBookings);
		return allBookings;
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
														@RequestParam(defaultValue = "ALL") String state,
														@RequestParam(required = false) Integer from,
														@RequestParam(required = false) Integer size) {
		log.info("Получен запрос на получение всех бронирований вещей владельца: {}, {}, from: {}, size: {}", userId, state, from, size);
		State validState = getState(state);
		List<BookingResponseDto> allBookings;
		if (from == null || size == null) {
			allBookings = bookingService.getAllOwnerBookings(userId, validState);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			allBookings = bookingService.getAllOwnerBookingsPageable(userId, from, size);
		}

		log.info("Обработан запрос на получение всех бронирований вещей владельца: {}, {}", userId, validState);
		return allBookings;
	}

	private State getState(String state) {
		State validState;
		try {
			validState = State.valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
		}
		return validState;
	}
}
