package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public interface CommentShort {
	long getId();
	String getText();
	String getAuthorName();
	LocalDateTime getCreated();
}
