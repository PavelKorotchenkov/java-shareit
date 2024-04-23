package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "text")
public class CommentRequestDto {
	@NotEmpty @NotBlank
	private String text;
	private long itemId;
	private long authorId;
}
