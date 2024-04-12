package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
	ItemDto add(ItemCreateDto itemCreateDto);

	ItemDto update(ItemUpdateDto itemUpdateDto);

	ItemWithFullInfoDto getById(long itemId, long userId);

	List<ItemWithFullInfoDto> getAllByUserId(long ownerId);

	List<ItemDto> searchBy(String text, long userId);

	CommentResponseDto addComment(CommentRequestDto commentRequestDto);
}
