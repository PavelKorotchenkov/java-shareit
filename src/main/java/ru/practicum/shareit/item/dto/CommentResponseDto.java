package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CommentResponseDto {
	private long id;
	private String text;
	private String authorName;
	private String created;
}
