package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
	BookingResponseDto add(BookingRequestDto bookingRequestDto);

	BookingResponseDto approve(long userId, long bookingId, boolean status);

	BookingResponseDto getInfoById(long userId, long bookingId);

	List<BookingResponseDto> getAllBookings(long userId, State state, Pageable pageable);

	List<BookingResponseDto> getAllOwnerBookings(long userId, State state, Pageable pageable);

}
