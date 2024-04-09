package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingDtoMapper {
	public static BookingDto toDto(Booking booking) {
		return BookingDto.builder()
				.id(booking.getId())
				.start(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getStartDate()))
				.end(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getEndDate()))
				.status(booking.getStatus())
				.booker(booking.getBooker() != null ? UserDtoMapper.toDtoId(booking.getBooker()) : null)
				.item(booking.getItem() != null ? ItemDtoMapper.toDtoIdName(booking.getItem()) : null)
				.build();
	}

	public static BookingDto toDtoItemOwner(Booking booking) {
		return BookingDto.builder()
				.id(booking.getId())
				.start(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getStartDate()))
				.end(DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(booking.getEndDate()))
				.status(booking.getStatus())
				.booker(booking.getBooker() != null ? UserDtoMapper.toDtoId(booking.getBooker()) : null)
				.item(booking.getItem() != null ? ItemDtoMapper.toDtoIdNameOwner(booking.getItem()) : null)
				.build();
	}

	public static BookingShortDto toShortDto(Booking booking) {
		return BookingShortDto.builder()
				.id(booking.getId())
				.bookerId(booking.getBooker() != null ? booking.getBooker().getId() : 0)
				.build();
	}

	public static Booking toBooking(BookingDto bookingDto) {
		return Booking.builder()
				.id(bookingDto.getId())
				.startDate(bookingDto.getStart() == null ? null : LocalDateTime
						.parse(bookingDto.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.endDate(bookingDto.getEnd() == null ? null : LocalDateTime
						.parse(bookingDto.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.build();
	}

	public static Booking toBooking(BookingCreateDto bookingCreateDto) {
		return Booking.builder()
				.startDate(bookingCreateDto.getStart() == null ? null : LocalDateTime
						.parse(bookingCreateDto.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.endDate(bookingCreateDto.getEnd() == null ? null : LocalDateTime
						.parse(bookingCreateDto.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
				.build();
	}
}
