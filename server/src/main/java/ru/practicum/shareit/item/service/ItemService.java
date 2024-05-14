package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
	ItemDto add(ItemCreateDto itemCreateDto);

	ItemDto update(ItemUpdateDto itemUpdateDto);

	ItemWithFullInfoDto getById(long itemId, long userId);

	List<ItemWithFullInfoDto> findByOwnerId(long ownerId, Pageable pageable);

	List<ItemDto> searchBy(long userId, String text, Pageable pageable);

	CommentResponseDto addComment(CommentRequestDto commentRequestDto);
}
