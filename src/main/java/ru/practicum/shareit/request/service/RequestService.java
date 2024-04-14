package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface RequestService {
	ItemRequestResponseDto addRequest(ItemRequestCreateDto itemRequestDto, long userId);
	List<ItemRequestResponseDto> getRequests(long userId);
	List<ItemRequestResponseDto> getOtherRequestsPageable(long userId, int from, int size);

	List<ItemRequestResponseDto> getOtherRequests(long userId);
	ItemRequestResponseDto getRequestById(long userId, long requestId);
}
