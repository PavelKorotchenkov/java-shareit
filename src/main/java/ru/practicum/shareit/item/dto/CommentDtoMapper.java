package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentDtoMapper {

	public static CommentRequestDto toDto(Comment comment) {
		return CommentRequestDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.itemId(comment.getItem().getId())
				.authorId(comment.getAuthor().getId())
				.created(comment.getCreated() != null ? DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(comment.getCreated()) : null)
				.build();
	}

	public static CommentResponseDto toResponseDto(Comment comment) {
		return CommentResponseDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.authorName(comment.getAuthor().getName())
				.created(comment.getCreated() != null ? DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(comment.getCreated()) : null)
				.build();
	}

	public static Comment toComment(CommentRequestDto commentRequestDto) {
		return Comment.builder()
				.id(commentRequestDto.getId())
				.text(commentRequestDto.getText())
				.created(commentRequestDto.getCreated() != null ?
						LocalDateTime.parse(commentRequestDto.getCreated(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null)
				.build();
	}
}
