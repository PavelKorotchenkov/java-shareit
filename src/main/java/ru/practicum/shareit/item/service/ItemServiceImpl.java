package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final BookingRepository bookingRepository;
	private final UserService userService;

	@Override
	public ItemDto add(ItemCreateDto itemCreateDto) {
		User owner = UserDtoMapper.toUser(userService.getById(itemCreateDto.getOwnerId()));
		Item item = ItemDtoMapper.toItemCreate(itemCreateDto);
		item.setUser(owner);
		return ItemDtoMapper.toDto(itemRepository.save(item));
	}

	@Override
	public ItemDto update(ItemUpdateDto itemUpdateDto) {
		long ownerId = itemUpdateDto.getOwnerId();
		User owner = UserDtoMapper.toUser(userService.getById(ownerId));
		Item checkItem = getItem(itemUpdateDto.getId());
		if (!Objects.equals(ownerId, checkItem.getUser().getId())) {
			throw new AccessDeniedException("Доступ к редактированию запрещен, " +
					"только владелец может редактировать вещь.");
		}
		Item itemToUpdate = ItemDtoMapper.toItem(getById(itemUpdateDto.getId(), ownerId));
		itemToUpdate.setUser(owner);

		if (itemUpdateDto.getName() != null) {
			itemToUpdate.setName(itemUpdateDto.getName());
		}
		if (itemUpdateDto.getDescription() != null) {
			itemToUpdate.setDescription(itemUpdateDto.getDescription());
		}
		if (itemUpdateDto.getAvailable() != null) {
			itemToUpdate.setIsAvailable(itemUpdateDto.getAvailable());
		}

		return ItemDtoMapper.toDto(itemRepository.save(itemToUpdate));
	}

	@Override
	public ItemWithBookingsDto getById(Long itemId, long userId) {
		userService.getById(userId);
		Item item = getItem(itemId);
		Booking lastOpt = null;
		Booking nextOpt = null;
		if (isOwner(userId, item)) {
			lastOpt = bookingRepository.findTop1ByItemUserIdAndEndDateBeforeOrderByEndDateDesc(userId, LocalDateTime.now());
			nextOpt = bookingRepository.findTop1ByItemUserIdAndStartDateAfterOrderByStartDateAsc(userId, LocalDateTime.now());
		}
		return makeItemWithBookingsDto(item, lastOpt, nextOpt);
	}

	@Override
	public List<ItemWithBookingsDto> findByUserId(long userId) {
		UserDto owner = userService.getById(userId);
		Map<Long, Item> itemMap = itemRepository.findByUserId(owner.getId())
				.stream()
				.collect(Collectors.toMap(Item::getId, Function.identity()));
		Map<Item, List<Booking>> pastBookings = bookingRepository.findByItemIdAndEndDateBeforeOrderByEndDateDesc(itemMap.keySet(), LocalDateTime.now())
				.stream()
				.collect(Collectors.groupingBy(Booking::getItem));
		Map<Item, List<Booking>> nextBookings = bookingRepository.findByItemIdAndStartDateAfterOrderByStartDateAsc(itemMap.keySet(), LocalDateTime.now())
				.stream()
				.collect(Collectors.groupingBy(Booking::getItem));

		return itemMap.values()
				.stream()
				.map(item -> {
					if (isOwner(userId, item)) {
						return makeItemWithBookingsDto(item,
								pastBookings.get(item) == null ? null : pastBookings.get(item).get(0),
								nextBookings.get(item) == null? null : nextBookings.get(item).get(0));
					} else {
						return makeItemWithBookingsDto(item, null, null);
					}
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemDto> searchBy(String text, long userId) {
		UserDto userDto = userService.getById(userId);
		if (text.isEmpty() || text.isBlank()) {
			return Collections.emptyList();
		}
		return itemRepository.findByNameOrDescriptionContainingAndAvailableTrue(text.toLowerCase())
				.stream()
				.map(ItemDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	private Item getItem(Long id) {
		Optional<Item> optItem = itemRepository.findById(id);
		return optItem.orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + id));
	}

	private ItemWithBookingsDto makeItemWithBookingsDto(Item item, Booking lastBooking, Booking nextBooking) {
		ItemWithBookingsDto itemWithBookingsDto = ItemDtoMapper.toItemWithBookingsDto(item);
		if (lastBooking != null) {
			BookingShortDto last = BookingDtoMapper.toShortDto(lastBooking);
			itemWithBookingsDto.setLastBooking(last);
		}
		if (nextBooking != null) {
			BookingShortDto next = BookingDtoMapper.toShortDto(nextBooking);
			itemWithBookingsDto.setNextBooking(next);
		}
		return itemWithBookingsDto;
	}

	private static boolean isOwner(long userId, Item item) {
		return item.getUser().getId() == userId;
	}
}
