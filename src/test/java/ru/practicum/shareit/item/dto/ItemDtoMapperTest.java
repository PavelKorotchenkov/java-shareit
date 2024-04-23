package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoMapperTest {

	@Autowired
	private JacksonTester<ItemDto> itemDtoJacksonTester;

	@Autowired
	private JacksonTester<ItemForRequestDto> itemForRequestDtoJacksonTester;

	@SneakyThrows
	@Test
	void toDto() {
		ItemDto itemDto = ItemDto.builder()
				.id(1L)
				.name("item")
				.description("description")
				.available(true)
				.ownerId(1L)
				.requestId(1L)
				.build();

		JsonContent<ItemDto> result = itemDtoJacksonTester.write(itemDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
		assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
		assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
	}

	@SneakyThrows
	@Test
	void toItemForRequestDto() {
		Item item = Item.builder()
				.id(1L)
				.name("name")
				.description("description")
				.available(false)
				.request(ItemRequest.builder().id(1L).build())
				.build();
		ItemForRequestDto itemForRequestDto = ItemDtoMapper.toItemForRequestDto(item);

		JsonContent<ItemForRequestDto> result = itemForRequestDtoJacksonTester.write(itemForRequestDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
		assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
	}
}