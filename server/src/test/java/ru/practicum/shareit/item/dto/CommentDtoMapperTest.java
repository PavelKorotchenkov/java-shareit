package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoMapperTest {

	@Autowired
	private JacksonTester<CommentResponseDto> commentResponseDtoJacksonTester;
	@Autowired
	private JacksonTester<Comment> commentJacksonTester;

	@SneakyThrows
	@Test
	void toResponseDto_whenCreatedNotNull_thenReturnOk() {
		Comment comment = Comment.builder()
				.id(1L)
				.text("text")
				.author(User.builder().name("author").build())
				.created(LocalDateTime.of(2024, 1, 10, 10, 10, 10))
				.build();

		CommentResponseDto responseDto = CommentDtoMapper.toCommentResponseDto(comment);
		JsonContent<CommentResponseDto> result = commentResponseDtoJacksonTester.write(responseDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
		assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-10T10:10:10");
	}

	@SneakyThrows
	@Test
	void toResponseDto_whenCreatedNull_thenReturnOk() {
		Comment comment = Comment.builder()
				.id(1L)
				.text("text")
				.author(User.builder().name("author").build())
				.created(null)
				.build();

		CommentResponseDto responseDto = CommentDtoMapper.toCommentResponseDto(comment);
		JsonContent<CommentResponseDto> result = commentResponseDtoJacksonTester.write(responseDto);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
		assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(null);
	}

	@SneakyThrows
	@Test
	void toComment() {
		CommentRequestDto commentRequestDto = CommentRequestDto.builder()
				.text("text")
				.build();

		Comment comment = CommentDtoMapper.ofCommentRequestDto(commentRequestDto);
		JsonContent<Comment> result = commentJacksonTester.write(comment);
		assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
	}
}