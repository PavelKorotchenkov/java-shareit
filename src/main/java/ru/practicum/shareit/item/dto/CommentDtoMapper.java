package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.format.DateTimeFormatter;

public class CommentDtoMapper {

	public static CommentResponseDto toCommentResponseDto(Comment comment) {
		return CommentResponseDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.authorName(comment.getAuthor().getName())
				.created(comment.getCreated() != null ? DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
						.format(comment.getCreated()) : null)
				.build();
	}

	public static Comment ofCommentRequestDto(CommentRequestDto commentRequestDto) {
		return Comment.builder()
				.text(commentRequestDto.getText())
				.build();
	}
}
