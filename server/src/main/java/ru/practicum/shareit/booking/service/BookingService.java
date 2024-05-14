package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
	BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto);

	BookingResponseDto approve(long userId, long bookingId, boolean status);

	BookingResponseDto getInfoById(long userId, long bookingId);

	List<BookingResponseDto> getAllBookings(long userId, BookingState state, Pageable pageable);

	List<BookingResponseDto> getAllOwnerBookings(long userId, BookingState state, Pageable pageable);

}
