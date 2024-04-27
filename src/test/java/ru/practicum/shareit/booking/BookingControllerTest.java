package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

		BookingResponseDto responseEntity = bookingController.addBooking(X_SHARER_USER_ID, request);

		assertEquals(Status.WAITING, Objects.requireNonNull(responseEntity.getStatus()));
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

		BookingResponseDto responseEntity = bookingController.approve(X_SHARER_USER_ID, bookingId, approved);
		assertEquals(Status.APPROVED, Objects.requireNonNull(responseEntity.getStatus()));
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

		BookingResponseDto responseEntity = bookingController.approve(X_SHARER_USER_ID, bookingId, approved);
		assertEquals(Status.REJECTED, Objects.requireNonNull(responseEntity.getStatus()));
	}

	@Test
	void getBookingInfoById_whenInvoked_thenReturnBooking() {
		long bookingId = 1L;
		long userId = 1L;
		BookingResponseDto expectedBookingResponseDto = BookingResponseDto.builder().id(1L).build();
		when(bookingService.getInfoById(userId, bookingId)).thenReturn(expectedBookingResponseDto);
		BookingResponseDto responseEntity = bookingController.getBookingInfoById(X_SHARER_USER_ID, bookingId);
		assertEquals(expectedBookingResponseDto.getId(), responseEntity.getId());
	}

	@Test
	void getAllBookings_whenInvoked_thenReturnList() {
		List<BookingResponseDto> expectedBookingResponseDtoList = List.of(BookingResponseDto.builder().id(1L).build());
		when(bookingService.getAllBookings(anyLong(), any(), any())).thenReturn(expectedBookingResponseDtoList);
		List<BookingResponseDto> responseEntity = bookingController.getAllBookings(X_SHARER_USER_ID, "ALL", 0, 1);
		assertEquals(expectedBookingResponseDtoList.get(0).getId(), responseEntity.get(0).getId());
	}

	@Test
	void getAllOwnerBookings_whenInvoked_thenReturnList() {
		List<BookingResponseDto> expectedBookingResponseDtoList = List.of(BookingResponseDto.builder().id(1L).build());
		when(bookingService.getAllOwnerBookings(anyLong(), any(), any())).thenReturn(expectedBookingResponseDtoList);
		List<BookingResponseDto> responseEntity = bookingController.getAllOwnerBookings(X_SHARER_USER_ID, "ALL", 0, 1);
		assertEquals(expectedBookingResponseDtoList.get(0).getId(), responseEntity.get(0).getId());
	}
}