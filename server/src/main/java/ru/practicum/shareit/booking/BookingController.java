package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final BookingService bookingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponseDto addBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
										 @RequestBody BookingRequestDto bookingRequestDto) {
		BookingResponseDto savedBooking = bookingService.add(userId, bookingRequestDto);
		log.info("Обработан запрос на бронирование вещи = {}, userId = {}", savedBooking, userId);
		return savedBooking;
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto approve(@RequestHeader(X_SHARER_USER_ID) long userId,
									  @PathVariable long bookingId,
									  @RequestParam boolean approved) {
		BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
		log.info("Обработан запрос - решение владельца вещи по одобрению бронирования = {}, userId = {}, bookingId = {}", bookingResponseDto, userId, bookingId);
		return bookingResponseDto;
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto getBookingInfoById(@RequestHeader(X_SHARER_USER_ID) long userId,
												 @PathVariable long bookingId) {
		BookingResponseDto bookingResponseDto = bookingService.getInfoById(userId, bookingId);
		log.info("Обработан запрос на получение информации о бронировании с bookingId = {}, userId = {}", bookingId, userId);
		return bookingResponseDto;
	}

	@GetMapping
	public List<BookingResponseDto> getAllBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
												   @RequestParam(defaultValue = "ALL") String state,
												   @RequestParam(defaultValue = "0") int from,
												   @RequestParam(defaultValue = "10") int size) {
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		BookingState validState = BookingState.valueOf(state);
		List<BookingResponseDto> allBookings = bookingService.getAllBookings(userId, validState, page);
		log.info("Обработан запрос на получение всех бронирований пользователя с userId = {}, бронирования = {}", userId, allBookings);
		return allBookings;
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
														@RequestParam(defaultValue = "ALL") String state,
														@RequestParam(defaultValue = "0") int from,
														@RequestParam(defaultValue = "10") int size) {
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		BookingState validState = BookingState.valueOf(state);
		List<BookingResponseDto> allBookings = bookingService.getAllOwnerBookings(userId, validState, page);
		log.info("Обработан запрос на получение всех бронирований вещей владельца с userId = {}, со статусом = {}", userId, validState);
		return allBookings;
	}
}
