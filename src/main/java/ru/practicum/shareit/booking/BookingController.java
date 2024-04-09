package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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
	public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
								 @Valid @RequestBody BookingCreateDto bookingCreateDto) {
		log.info("Получен запрос на бронирование вещи: {}", bookingCreateDto);
		bookingCreateDto.setBookerId(userId);
		BookingDto savedBooking = bookingService.add(bookingCreateDto);
		log.info("Обработан запрос на бронирование вещи: {}", savedBooking);
		return savedBooking;
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
							  @PathVariable long bookingId,
							  @RequestParam boolean approved) {
		log.info("Получен запрос - решение владельца вещи по одобрению бронирования: " +
				"user id - {}, booking id - {}, approved - {}", userId, bookingId, approved);
		BookingDto bookingDto = bookingService.approve(userId, bookingId, approved);
		log.info("Обработан запрос - решение владельца вещи по одобрению бронирования: {}", bookingDto);
		return bookingDto;
	}

	@GetMapping("/{bookingId}")
	public BookingDto getBookingInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
										 @PathVariable long bookingId) {
		log.info("Получен запрос на получение информации о бронировании: {}", bookingId);
		BookingDto bookingDto = bookingService.getInfoById(userId, bookingId);
		log.info("Обработан запрос на получение информации о бронировании: {}", bookingId);
		return bookingDto;
	}

	@GetMapping
	public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestParam(defaultValue = "ALL") String state) {
		log.info("Получен запрос на получение всех бронирований пользователя: {}, {}", userId, state);
		List<BookingDto> allBookings = bookingService.getAllBookings(userId, state);
		log.info("Обработан запрос на получение всех бронирований пользователя: {}, {}", userId, state);
		return allBookings;
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestParam(defaultValue = "ALL") String state) {
		log.info("Получен запрос на получение всех бронирований вещей владельца: {}, {}", userId, state);
		List<BookingDto> allBookings = bookingService.getAllOwnerBookings(userId, state);
		log.info("Обработан запрос на получение всех бронирований вещей владельца: {}, {}", userId, state);
		return allBookings;
	}

}
