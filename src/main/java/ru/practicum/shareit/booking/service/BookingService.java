package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
	BookingDto add(BookingCreateDto bookingCreateDto);
	BookingDto approve(long userId, long bookingId, boolean status);

	BookingDto getInfoById(long userId, long bookingId);

	List<BookingDto> getAllBookings(long userId, String state);
	List<BookingDto> getAllOwnerBookings(long userId, String state);

}
