package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

	/*@Test
	void toResponseDto() throws Exception {
		// Arrange
		ItemRequest itemRequest = ItemRequest.builder()
				.id(1L)
				.description("Test description")
				.items(Collections.singletonList(new Item()))
				.build();

		// Act
		ItemRequestResponseDto responseDto = ItemRequestDtoMapper.toResponseDto(itemRequest);

		// Assert
		assertNotNull(responseDto);
		assertEquals(itemRequest.getId(), responseDto.getId());
		assertEquals(itemRequest.getDescription(), responseDto.getDescription());
	}*/
}