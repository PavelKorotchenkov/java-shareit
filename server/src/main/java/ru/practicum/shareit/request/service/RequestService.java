package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface RequestService {
	ItemRequestResponseDto addRequest(ItemRequestCreateDto itemRequestDto, long userId);

	List<ItemRequestResponseDto> getRequests(long userId);

	List<ItemRequestResponseDto> getOtherRequests(long userId, Pageable pageable);

	ItemRequestResponseDto getRequestById(long userId, long requestId);
}
