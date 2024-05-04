package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoMapperTest {
	@Autowired
	private JacksonTester<BookingResponseDto> bookingResponseDtoJacksonTester;

	@SneakyThrows
	@Test
	void toDto() {
		BookingResponseDto responseDto = BookingResponseDto.builder()
				.id(1L)
				.start("2024-04-10'T'10:10:10")
				.end("2024-05-10'T'10:10:10")
				.status(Status.APPROVED)
				.booker(UserDto.builder().build())
				.item(ItemDto.builder().build())
				.build();
		JsonContent<BookingResponseDto> result = bookingResponseDtoJacksonTester.write(responseDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-04-10'T'10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-05-10'T'10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.name());
	}
}