package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserService userService;

	@Autowired

	public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
		this.itemRepository = itemRepository;
		this.userService = userService;
	}

	@Override
	public ItemDto add(ItemCreateDto itemCreateDto, Long ownerId) {
		User owner = UserDtoMapper.toUser(userService.getById(ownerId));
		Item item = ItemDtoMapper.toItemCreate(itemCreateDto);
		item.setOwner(owner);
		return ItemDtoMapper.toDto(itemRepository.add(item));
	}

	@Override
	public ItemDto update(ItemUpdateDto itemUpdateDto, Long ownerId) {
		User owner = UserDtoMapper.toUser(userService.getById(ownerId));
		Item checkItem = checkItem(itemUpdateDto.getId());
		if (!Objects.equals(ownerId, checkItem.getOwner().getId())) {
			throw new AccessDeniedException("Доступ к редактированию запрещен, " +
					"только владелец может редактировать вещь.");
		}
		Item itemToUpdate = ItemDtoMapper.toItem(getById(itemUpdateDto.getId(), ownerId));
		itemToUpdate.setOwner(owner);

		if (itemUpdateDto.getName() != null) {
			itemToUpdate.setName(itemUpdateDto.getName());
		}
		if (itemUpdateDto.getDescription() != null) {
			itemToUpdate.setDescription(itemUpdateDto.getDescription());
		}
		if (itemUpdateDto.getAvailable() != null) {
			itemToUpdate.setAvailable(itemUpdateDto.getAvailable());
		}

		return ItemDtoMapper.toDto(itemRepository.update(itemToUpdate));
	}

	@Override
	public ItemDto getById(Long itemId, Long userId) {
		userService.getById(userId);
		return ItemDtoMapper.toDto(checkItem(itemId));
	}

	@Override
	public List<ItemDto> findByOwnerId(Long ownerId) {
		UserDto owner = userService.getById(ownerId);
		return itemRepository.findByOwnerId(owner.getId())
				.stream()
				.map(this::checkItem)
				.map(ItemDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemDto> searchBy(String text, Long userId) {
		UserDto owner = userService.getById(userId);
		if (text.isEmpty() || text.isBlank()) {
			return new ArrayList<>();
		}
		return itemRepository.searchBy(text)
				.stream()
				.map(ItemDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	private Item checkItem(Long id) {
		Optional<Item> optItem = itemRepository.getById(id);
		return optItem.orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + id));
	}
}
