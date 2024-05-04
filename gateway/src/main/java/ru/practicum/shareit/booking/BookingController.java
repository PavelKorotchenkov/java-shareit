package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @RequestBody @Valid BookingRequestDto bookingRequestDto) {
		log.info("Получен запрос на бронирование вещи = {}, userId = {}", bookingRequestDto, userId);
		return bookingClient.addBooking(userId, bookingRequestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader(X_SHARER_USER_ID) long userId,
										  @PathVariable long bookingId,
										  @RequestParam boolean approved) {
		log.info("Получен запрос - решение владельца вещи по одобрению бронирования: " +
				"ownerId = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
		return bookingClient.approve(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingInfoById(@RequestHeader(X_SHARER_USER_ID) long userId,
													 @PathVariable long bookingId) {
		log.info("Получен запрос на получение информации о бронировании = {}, userId = {}", bookingId, userId);
		return bookingClient.getBooking(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
												 @RequestParam(defaultValue = "ALL") String state,
												 @RequestParam(defaultValue = "0") int from,
												 @RequestParam(defaultValue = "10") int size) {
		BookingState validState = BookingState.getState(state).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Получен запрос на получение всех бронирований пользователя userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
		return bookingClient.getBookings(userId, validState, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
													  @RequestParam(defaultValue = "ALL") String state,
													  @RequestParam(defaultValue = "0") int from,
													  @RequestParam(defaultValue = "10") int size) {
		log.info("Получен запрос на получение всех бронирований вещей владельца userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
		BookingState validState = BookingState.getState(state).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		return bookingClient.getOwnerBookings(userId, validState, from, size);
	}
}
