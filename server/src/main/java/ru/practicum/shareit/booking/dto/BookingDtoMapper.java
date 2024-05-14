package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingDtoMapper {
	public static Booking ofBookingRequestDto(BookingRequestDto bookingRequestDto) {
		return Booking.builder()
				.startDate(bookingRequestDto.getStart() == null ? null : LocalDateTime
						.parse(bookingRequestDto.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.endDate(bookingRequestDto.getEnd() == null ? null : LocalDateTime
						.parse(bookingRequestDto.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.build();
	}

	public static BookingResponseDto toBookingResponseDto(Booking booking) {
		return BookingResponseDto.builder()
				.id(booking.getId())
				.start(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getStartDate()))
				.end(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getEndDate()))
				.status(booking.getStatus())
				.booker(booking.getBooker() != null ? UserDtoMapper.toUserDtoId(booking.getBooker()) : null)
				.item(booking.getItem() != null ? ItemDtoMapper.toItemDto(booking.getItem()) : null)
				.build();
	}

	public static BookingShortDto toBookingShortDto(Booking booking) {
		return BookingShortDto.builder()
				.id(booking.getId())
				.bookerId(booking.getBooker() != null ? booking.getBooker().getId() : 0)
				.build();
	}

	public static List<BookingResponseDto> toBookingResponseDto(List<Booking> bookingRepository) {
		return bookingRepository
				.stream()
				.map(BookingDtoMapper::toBookingResponseDto)
				.collect(Collectors.toList());
	}
}
