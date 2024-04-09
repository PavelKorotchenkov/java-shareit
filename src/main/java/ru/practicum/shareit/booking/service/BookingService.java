package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
	BookingResponseDto add(BookingRequestDto bookingRequestDto);

	BookingResponseDto approve(long userId, long bookingId, boolean status);

	BookingResponseDto getInfoById(long userId, long bookingId);

	List<BookingResponseDto> getAllBookings(long userId, String state);

	List<BookingResponseDto> getAllOwnerBookings(long userId, String state);

}