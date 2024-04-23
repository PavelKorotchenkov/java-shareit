package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<BookingResponseDto> addBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
														 @Valid @RequestBody BookingRequestDto bookingRequestDto) {
		log.info("Получен запрос на бронирование вещи: {}", bookingRequestDto);
		bookingRequestDto.setBookerId(userId);
		BookingResponseDto savedBooking = bookingService.add(bookingRequestDto);
		log.info("Обработан запрос на бронирование вещи: {}", savedBooking);
		return ResponseEntity.ok(savedBooking);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<BookingResponseDto> approve(@RequestHeader(X_SHARER_USER_ID) long userId,
													  @PathVariable long bookingId,
													  @RequestParam boolean approved) {
		log.info("Получен запрос - решение владельца вещи по одобрению бронирования: " +
				"user id - {}, booking id - {}, approved - {}", userId, bookingId, approved);
		BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
		log.info("Обработан запрос - решение владельца вещи по одобрению бронирования: {}", bookingResponseDto);
		return ResponseEntity.ok(bookingResponseDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingResponseDto> getBookingInfoById(@RequestHeader(X_SHARER_USER_ID) long userId,
																 @PathVariable long bookingId) {
		log.info("Получен запрос на получение информации о бронировании: {}", bookingId);
		BookingResponseDto bookingResponseDto = bookingService.getInfoById(userId, bookingId);
		log.info("Обработан запрос на получение информации о бронировании: {}", bookingId);
		return ResponseEntity.ok(bookingResponseDto);
	}

	@GetMapping
	public ResponseEntity<List<BookingResponseDto>> getAllBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
																   @RequestParam(defaultValue = "ALL") String state,
																   @RequestParam(required = false) Integer from,
																   @RequestParam(required = false) Integer size) {
		log.info("Получен запрос на получение всех бронирований пользователя: user id: {}, state: {}", userId, state);
		State validState = getState(state);
		PageRequest page = createPageRequest(from, size);
		List<BookingResponseDto> allBookings = bookingService.getAllBookings(userId, validState, page);
		log.info("Обработан запрос на получение всех бронирований пользователя: {}", allBookings);
		return ResponseEntity.ok(allBookings);
	}

	@GetMapping("/owner")
	public ResponseEntity<List<BookingResponseDto>> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
																		@RequestParam(defaultValue = "ALL") String state,
																		@RequestParam(required = false) Integer from,
																		@RequestParam(required = false) Integer size) {
		log.info("Получен запрос на получение всех бронирований вещей владельца: {}, {}", userId, state);
		State validState = getState(state);
		PageRequest page = createPageRequest(from, size);
		List<BookingResponseDto> allBookings = bookingService.getAllOwnerBookings(userId, validState, page);
		log.info("Обработан запрос на получение всех бронирований вещей владельца: {}, {}", userId, validState);
		return ResponseEntity.ok(allBookings);
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

	private PageRequest createPageRequest(Integer from, Integer size) {
		PageRequest page;
		if (from == null || size == null) {
			page = PageRequest.of(0, Integer.MAX_VALUE);
		} else if (from < 0) {
			throw new IllegalArgumentException("Параметр 'from' должен быть больше нуля.");
		} else {
			page = PageRequest.of(from > 0 ? from / size : 0, size);
		}
		return page;
	}
}
