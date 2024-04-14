package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
	BookingResponseDto add(BookingRequestDto bookingRequestDto);

	BookingResponseDto approve(long userId, long bookingId, boolean status);

	BookingResponseDto getInfoById(long userId, long bookingId);

	List<BookingResponseDto> getAllBookings(long userId, State state);

	List<BookingResponseDto> getAllOwnerBookings(long userId, State state);

	List<BookingResponseDto> getAllOwnerBookingsPageable(long userId, Integer from, Integer size);

	List<BookingResponseDto> getAllBookingsPageable(long userId, Integer from, Integer size);
}
