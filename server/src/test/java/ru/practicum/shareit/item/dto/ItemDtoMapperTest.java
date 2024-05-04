package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoMapperTest {

	@Autowired
	private JacksonTester<ItemDto> itemDtoJacksonTester;

	@SneakyThrows
	@Test
	void toDto() {
		ItemDto itemDto = ItemDto.builder()
				.id(1L)
				.name("item")
				.description("description")
				.available(true)
				.requestId(1L)
				.build();

		JsonContent<ItemDto> result = itemDtoJacksonTester.write(itemDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
		assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
	}
}