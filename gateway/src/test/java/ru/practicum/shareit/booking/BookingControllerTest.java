package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

	public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	public static final long USER_ID = 1L;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingClient client;
	private LocalDateTime start;
	private LocalDateTime end;

	@SneakyThrows
	@Test
	void addBooking_whenBookingTimeValid_thenReturnStatusOk() {
		start = LocalDateTime.now().plusDays(1);
		end = LocalDateTime.now().plusDays(2);

		BookingRequestDto request = BookingRequestDto.builder()
				.start(start)
				.end(end)
				.build();

		mockMvc.perform(post("/bookings")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().is2xxSuccessful());
	}

	@SneakyThrows
	@Test
	void addBooking_whenBookingTimeNotValid_thenReturnStatusBadRequest() {
		start = LocalDateTime.now().minusDays(1);
		end = LocalDateTime.now().plusDays(2);

		BookingRequestDto request = BookingRequestDto.builder()
				.start(start)
				.end(end)
				.build();

		mockMvc.perform(post("/bookings")
						.header(X_SHARER_USER_ID, USER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	@Test
	void approve() {
		long bookingId = 1L;
		mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
						.header(X_SHARER_USER_ID, USER_ID)
						.param("approved", String.valueOf(true)))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getBookingInfoById() {
		long bookingId = 1L;
		mockMvc.perform(get("/bookings/{bookingId}", bookingId)
						.header(X_SHARER_USER_ID, USER_ID))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenValidState_thenReturnStatusOk() {
		mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, USER_ID)
						.param("state", "ALL")
						.param("from", String.valueOf(0))
						.param("size", String.valueOf(10)))
				.andExpect(status().is2xxSuccessful());
	}

	@SneakyThrows
	@Test
	void getAllBookings_whenNotValidState_thenReturnStatusBadRequest() {
		mockMvc.perform(get("/bookings")
						.header(X_SHARER_USER_ID, USER_ID)
						.param("state", "INVALID")
						.param("from", String.valueOf(0))
						.param("size", String.valueOf(10)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Unknown state: INVALID"));
	}

	@SneakyThrows
	@Test
	void getAllOwnerBookings_whenValidState_thenReturnStatusOk() {
		mockMvc.perform(get("/bookings/owner")
						.header(X_SHARER_USER_ID, USER_ID)
						.param("state", "ALL")
						.param("from", String.valueOf(0))
						.param("size", String.valueOf(10)))
				.andExpect(status().is2xxSuccessful());
	}

	@SneakyThrows
	@Test
	void getAllOwnerBookings_whenNotValidState_thenReturnStatusBadRequest() {
		mockMvc.perform(get("/bookings/owner")
						.header(X_SHARER_USER_ID, USER_ID)
						.param("state", "INVALID")
						.param("from", String.valueOf(0))
						.param("size", String.valueOf(10)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Unknown state: INVALID"));
	}
}