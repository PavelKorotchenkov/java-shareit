package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@ToString
public class CommentRequestDto {
	private long id;
	@NotEmpty @NotBlank
	private String text;
	private long itemId;
	private long authorId;
	private String created;
}
