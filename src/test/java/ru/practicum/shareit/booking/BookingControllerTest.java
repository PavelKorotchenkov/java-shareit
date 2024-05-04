package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private BookingService bookingService;
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

	@SneakyThrows
	@Test
	void addBooking_whenValidParams_thenReturnStatusOkWithBookingResponseDto() {
		BookingRequestDto request = BookingRequestDto.builder()
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.build();
		UserDto userDto = UserDto.builder().email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(userDto)
				.item(itemDto)
				.build();

		when(bookingService.add(any())).thenReturn(response);

		String result = mockMvc.perform(post("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id", is(response.getId()), Long.class))
				.andExpect(jsonPath("$.start", is(response.getStart()), String.class))
				.andExpect(jsonPath("$.end", is(response.getEnd()), String.class))
				.andExpect(jsonPath("$.status", is(Status.WAITING.name()), String.class))
				.andExpect(jsonPath("$.booker.name", is(userDto.getName()), String.class))
				.andExpect(jsonPath("$.booker.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.item.name", is(itemDto.getName()), String.class))
				.andExpect(jsonPath("$.item.description", is(itemDto.getDescription()), String.class))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void addBooking_whenItemNotAvailable_thenThrowNotAvailableException() {
		BookingRequestDto request = BookingRequestDto.builder()
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.build();

		when(bookingService.add(any())).thenThrow(NotAvailableException.class);

		mockMvc.perform(post("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void addBooking_whenBookingTimeNotValid_thenThrowBookingDateException() {
		BookingRequestDto request = BookingRequestDto.builder()
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.build();

		when(bookingService.add(any())).thenThrow(BookingDateException.class);

		mockMvc.perform(post("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void approve_whenApproved_thenReturnStatusOkWithBookingResponseDtoWithStatusApproved() {
		long userId = 1L;
		long bookingId = 1L;
		boolean approved = true;
		UserDto userDto = UserDto.builder().email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.APPROVED)
				.booker(userDto)
				.item(itemDto)
				.build();
		when(bookingService.approve(userId, bookingId, approved)).thenReturn(response);

		String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
						.header(X_SHARER_USER_ID, 1L)
						.param("approved", String.valueOf(approved)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(response.getId()), Long.class))
				.andExpect(jsonPath("$.start", is(response.getStart()), String.class))
				.andExpect(jsonPath("$.end", is(response.getEnd()), String.class))
				.andExpect(jsonPath("$.status", is(Status.APPROVED.name()), String.class))
				.andExpect(jsonPath("$.booker.name", is(userDto.getName()), String.class))
				.andExpect(jsonPath("$.booker.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.item.name", is(itemDto.getName()), String.class))
				.andExpect(jsonPath("$.item.description", is(itemDto.getDescription()), String.class))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void approve_whenRejected_thenReturnStatusOkWithBookingResponseDtoWithStatusRejected() {
		long userId = 1L;
		long bookingId = 1L;
		boolean approved = false;
		UserDto userDto = UserDto.builder().email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.REJECTED)
				.booker(userDto)
				.item(itemDto)
				.build();
		when(bookingService.approve(userId, bookingId, approved)).thenReturn(response);

		String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
						.header(X_SHARER_USER_ID, 1L)
						.param("approved", String.valueOf(approved)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(response.getId()), Long.class))
				.andExpect(jsonPath("$.start", is(response.getStart()), String.class))
				.andExpect(jsonPath("$.end", is(response.getEnd()), String.class))
				.andExpect(jsonPath("$.status", is(Status.REJECTED.name()), String.class))
				.andExpect(jsonPath("$.booker.name", is(userDto.getName()), String.class))
				.andExpect(jsonPath("$.booker.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.item.name", is(itemDto.getName()), String.class))
				.andExpect(jsonPath("$.item.description", is(itemDto.getDescription()), String.class))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void getBookingInfoById_whenValidBookerIdAndOwnerId_thenReturnStatusOkWithBookingResponseDto() {
		long userId = 1L;
		long bookingId = 1L;
		UserDto userDto = UserDto.builder().id(userId).email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		BookingResponseDto response = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(userDto)
				.item(itemDto)
				.build();
		when(bookingService.getInfoById(userId, bookingId)).thenReturn(response);

		String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
						.header(X_SHARER_USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(response.getId()), Long.class))
				.andExpect(jsonPath("$.start", is(response.getStart()), String.class))
				.andExpect(jsonPath("$.end", is(response.getEnd()), String.class))
				.andExpect(jsonPath("$.status", is(Status.WAITING.name()), String.class))
				.andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
				.andExpect(jsonPath("$.booker.name", is(userDto.getName()), String.class))
				.andExpect(jsonPath("$.booker.email", is(userDto.getEmail()), String.class))
				.andExpect(jsonPath("$.item.name", is(itemDto.getName()), String.class))
				.andExpect(jsonPath("$.item.description", is(itemDto.getDescription()), String.class))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void getBookingInfoById_whenBookingNotFound_thenReturnNotFoundException() {
		when(bookingService.getInfoById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/bookings/{bookingId}", anyLong())
						.header(X_SHARER_USER_ID, anyLong()))
				.andExpect(status().isNotFound());
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenGetBookingsWithStateAll_thenReturnBookingsWithStateAll() {
		int from = 0;
		int size = 1;
		long userId = 1L;
		UserDto userDto = UserDto.builder().id(userId).email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		State state = State.ALL;
		Pageable page = PageRequest.of(from, size);
		BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(userDto)
				.item(itemDto)
				.build();
		List<BookingResponseDto> response = List.of(bookingResponseDto);

		when(bookingService.getAllBookings(userId, state, page)).thenReturn(response);

		String result = mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.param("state", State.ALL.name())
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenPageParamsNulls_thenReturnStatusOk() {
		long userId = 1L;
		UserDto userDto = UserDto.builder().id(userId).email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(userDto)
				.item(itemDto)
				.build();
		List<BookingResponseDto> response = List.of(bookingResponseDto);

		when(bookingService.getAllBookings(anyLong(), any(), any())).thenReturn(response);

		String result = mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.param("state", State.ALL.name())
						.param("from", (String) null)
						.param("size", (String) null))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenFromParamLessThanZero_thenThrowIllegalArgumentException() {
		int from = -1;
		int size = 1;
		mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.param("state", State.ALL.name())
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isInternalServerError())
				.andExpect(result -> {
					Throwable exception = result.getResolvedException();
					assertTrue(exception instanceof IllegalArgumentException);
					assertEquals("Параметр 'from' должен быть больше нуля.", exception.getMessage());
				});
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenNotValidState_thenThrowInvalidStateException() {
		int from = 0;
		int size = 1;
		mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, 1L)
						.param("state", "INVALID")
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void getAllOwnerBookings() {
		int from = 0;
		int size = 1;
		long userId = 1L;
		long ownerId = 2L;
		UserDto userDto = UserDto.builder().id(userId).email("mail@mail.ru").name("user").build();
		ItemDto itemDto = ItemDto.builder().name("item").description("desc").build();
		State state = State.ALL;
		Pageable page = PageRequest.of(from, size);
		BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
				.id(1L)
				.start(formattedDateTimeStart)
				.end(formattedDateTimeEnd)
				.status(Status.WAITING)
				.booker(userDto)
				.item(itemDto)
				.build();
		List<BookingResponseDto> response = List.of(bookingResponseDto);

		when(bookingService.getAllOwnerBookings(ownerId, state, page)).thenReturn(response);

		String result = mockMvc.perform(get("/bookings/owner")
						.header(X_SHARER_USER_ID, ownerId)
						.param("state", State.ALL.name())
						.param("from", String.valueOf(from))
						.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals(objectMapper.writeValueAsString(response), result);
	}
}