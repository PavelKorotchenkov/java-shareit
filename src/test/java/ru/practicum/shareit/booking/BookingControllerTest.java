package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
	@Mock
	private BookingService bookingService;
	@InjectMocks
	private BookingController bookingController;

	private static final long X_SHARER_USER_ID = 1L;
	private DateTimeFormatter formatter;
	private LocalDateTime start;
	private LocalDateTime end;
	private String formattedDateTimeStart;
	private String formattedDateTimeEnd;

	@BeforeEach
	void setUp() {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		start = LocalDateTime.now().plusDays(1);
		end = LocalDateTime.now().plusDays(2);
		formattedDateTimeStart = start.format(formatter);
		formattedDateTimeEnd = end.format(formatter);
	}

	@Test
	void addBooking_whenAddBooking_thenReturnStatusOkWithBookingResponseDtoInBody() {
		BookingRequestDto request = BookingRequestDto.builder()
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.build();
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(UserDto.builder().build())
				.item(ItemDto.builder().build())
				.build();
		when(bookingService.add(request)).thenReturn(response);

		ResponseEntity<BookingResponseDto> responseEntity = bookingController.addBooking(X_SHARER_USER_ID, request);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(Status.WAITING, Objects.requireNonNull(responseEntity.getBody()).getStatus());
	}

	@Test
	void approve_whenApprove_thenReturnStatusOkWithBookingResponseDtoInBodyWithStatusApproved() {
		long bookingId = 1L;
		boolean approved = true;
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.APPROVED)
				.booker(UserDto.builder().build())
				.item(ItemDto.builder().build())
				.build();
		when(bookingService.approve(X_SHARER_USER_ID, bookingId, approved)).thenReturn(response);

		ResponseEntity<BookingResponseDto> responseEntity = bookingController.approve(X_SHARER_USER_ID, bookingId, approved);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(Status.APPROVED, Objects.requireNonNull(responseEntity.getBody()).getStatus());
	}

	@Test
	void approve_whenReject_thenReturnStatusOkWithBookingResponseDtoInBodyWithStatusRejected() {
		long bookingId = 1L;
		boolean approved = false;
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.REJECTED)
				.booker(UserDto.builder().build())
				.item(ItemDto.builder().build())
				.build();
		when(bookingService.approve(X_SHARER_USER_ID, bookingId, approved)).thenReturn(response);

		ResponseEntity<BookingResponseDto> responseEntity = bookingController.approve(X_SHARER_USER_ID, bookingId, approved);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(Status.REJECTED, Objects.requireNonNull(responseEntity.getBody()).getStatus());
	}

	@Test
	void getBookingInfoById_whenInvoked_thenReturnStatusOk() {
		long bookingId = 1L;
		ResponseEntity<BookingResponseDto> responseEntity = bookingController.getBookingInfoById(X_SHARER_USER_ID, bookingId);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	void getAllBookings_whenInvoked_thenReturnStatusOk() {
		ResponseEntity<List<BookingResponseDto>> responseEntity = bookingController.getAllBookings(X_SHARER_USER_ID, "ALL", 0, 1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	void getAllOwnerBookings() {
		ResponseEntity<List<BookingResponseDto>> responseEntity = bookingController.getAllOwnerBookings(X_SHARER_USER_ID, "ALL", 0, 1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
}