package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoMapperTest {

	@Autowired
	private JacksonTester<ItemRequestCreateDto> itemRequestCreateDtoJson;

	@Autowired
	private JacksonTester<ItemRequestResponseDto> itemRequestResponseDtoJson;

	@Autowired
	private JacksonTester<ItemForRequestDto> itemForRequestDtoJson;

	@SneakyThrows
	@Test
	void toItemRequest() {
		ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
				.id(1L)
				.description("description").build();

		JsonContent<ItemRequestCreateDto> result = itemRequestCreateDtoJson.write(requestCreateDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
	}
}