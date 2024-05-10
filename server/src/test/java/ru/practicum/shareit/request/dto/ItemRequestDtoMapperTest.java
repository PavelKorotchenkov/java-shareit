package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoMapperTest {

	@Autowired
	private JacksonTester<ItemRequestCreateDto> itemRequestCreateDtoJson;

	@Autowired
	private JacksonTester<ItemRequestResponseDto> itemRequestResponseDtoJson;

	@Test
	void toItemRequest() throws IOException {
		ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
				.id(1L)
				.description("description").build();

		JsonContent<ItemRequestCreateDto> result = itemRequestCreateDtoJson.write(requestCreateDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
	}
}