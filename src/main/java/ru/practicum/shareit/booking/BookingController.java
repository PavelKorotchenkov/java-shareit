package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
										 @Valid @RequestBody BookingRequestDto bookingRequestDto) {
		log.info("Получен запрос на бронирование вещи: {}", bookingRequestDto);
		bookingRequestDto.setBookerId(userId);
		BookingResponseDto savedBooking = bookingService.add(bookingRequestDto);
		log.info("Обработан запрос на бронирование вещи: {}", savedBooking);
		return savedBooking;
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
									  @PathVariable long bookingId,
									  @RequestParam boolean approved) {
		log.info("Получен запрос - решение владельца вещи по одобрению бронирования: " +
				"user id - {}, booking id - {}, approved - {}", userId, bookingId, approved);
		BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
		log.info("Обработан запрос - решение владельца вещи по одобрению бронирования: {}", bookingResponseDto);
		return bookingResponseDto;
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto getBookingInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
												 @PathVariable long bookingId) {
		log.info("Получен запрос на получение информации о бронировании: {}", bookingId);
		BookingResponseDto bookingResponseDto = bookingService.getInfoById(userId, bookingId);
		log.info("Обработан запрос на получение информации о бронировании: {}", bookingId);
		return bookingResponseDto;
	}

	@GetMapping
	public List<BookingResponseDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId,
												   @RequestParam(defaultValue = "ALL") String state) {
		log.info("Получен запрос на получение всех бронирований пользователя: {}, {}", userId, state);
		List<BookingResponseDto> allBookings = bookingService.getAllBookings(userId, state);
		log.info("Обработан запрос на получение всех бронирований пользователя: {}, {}", userId, state);
		return allBookings;
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
														@RequestParam(defaultValue = "ALL") String state) {
		log.info("Получен запрос на получение всех бронирований вещей владельца: {}, {}", userId, state);
		List<BookingResponseDto> allBookings = bookingService.getAllOwnerBookings(userId, state);
		log.info("Обработан запрос на получение всех бронирований вещей владельца: {}, {}", userId, state);
		return allBookings;
	}
}
