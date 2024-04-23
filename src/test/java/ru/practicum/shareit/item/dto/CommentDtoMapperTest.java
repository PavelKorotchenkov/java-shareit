package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoMapperTest {

	@Autowired
	private JacksonTester<CommentResponseDto> commentResponseDtoJacksonTester;

	@Autowired
	private JacksonTester<Comment> commentJacksonTester;

	@SneakyThrows
	@Test
	void toResponseDto() {
		CommentResponseDto responseDto = CommentResponseDto.builder().id(1L).text("text").authorName("author")
				.created(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(LocalDateTime.of(2024, 1, 10, 10, 10, 10)))
				.build();

		JsonContent<CommentResponseDto> result = commentResponseDtoJacksonTester.write(responseDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
		assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-10T10:10:10");
	}
}