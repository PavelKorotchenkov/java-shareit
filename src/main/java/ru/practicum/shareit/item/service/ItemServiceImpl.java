package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
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
	private final CommentRepository commentRepository;
	private final UserService userService;
	private final RequestRepository requestRepository;
	private final Sort byStartDateAsc = Sort.by(Sort.Direction.ASC, "StartDate");
	private final Sort byEndDateDesc = Sort.by(Sort.Direction.DESC, "EndDate");

	@Override
	public ItemDto add(ItemCreateDto itemCreateDto) {
		User owner = UserDtoMapper.ofUserDto(userService.getById(itemCreateDto.getOwnerId()));
		Item item = ItemDtoMapper.ofItemCreateDto(itemCreateDto);
		item.setOwner(owner);
		setRequest(itemCreateDto, item);
		return ItemDtoMapper.toItemDto(itemRepository.save(item));
	}

	@Override
	public ItemDto update(ItemUpdateDto itemUpdateDto) {
		long userId = itemUpdateDto.getOwnerId();
		User user = UserDtoMapper.ofUserDto(userService.getById(userId));
		Item item = getItem(itemUpdateDto.getId());
		if (!Objects.equals(userId, item.getOwner().getId())) {
			throw new AccessDeniedException("Доступ к редактированию запрещен, " +
					"только владелец может редактировать вещь.");
		}
		Item itemToUpdate = ItemDtoMapper.ofItemWithFullInfoDto(getById(itemUpdateDto.getId(), userId));
		itemToUpdate.setOwner(user);

		setFieldsToUpdate(itemUpdateDto, itemToUpdate);

		return ItemDtoMapper.toItemDto(itemRepository.save(itemToUpdate));
	}

	@Override
	public ItemWithFullInfoDto getById(long itemId, long userId) {
		userService.getById(userId);
		Item item = getItem(itemId);
		Booking lastOpt = null;
		Booking nextOpt = null;
		if (isOwner(userId, item)) {
			lastOpt = bookingRepository.findTop1ByItemOwnerIdAndStartDateBeforeAndStatusIn(userId, LocalDateTime.now(), List.of(Status.APPROVED), byEndDateDesc);
			nextOpt = bookingRepository.findTop1ByItemOwnerIdAndStartDateAfterAndStatusIn(userId, LocalDateTime.now(), List.of(Status.APPROVED), byStartDateAsc);
		}
		ItemWithFullInfoDto itemWithFullInfoDto = makeItemWithBookingsDto(item, lastOpt, nextOpt);
		List<CommentShort> comments = commentRepository.findAllByItemId(itemId);
		itemWithFullInfoDto.setComments(comments);
		return itemWithFullInfoDto;
	}

	@Override
	public List<ItemWithFullInfoDto> findByOwnerId(long userId, Pageable pageable) {
		UserDto owner = userService.getById(userId);

		PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		Map<Long, Item> itemMap = itemRepository.findByOwnerId(owner.getId())
				.stream()
				.collect(Collectors.toMap(Item::getId, Function.identity()));

		Set<Long> itemIds = itemMap.keySet();

		Map<Item, List<Booking>> pastBookings = bookingRepository
				.findByItemIdAndEndDateBeforeOrderByEndDateDesc(itemIds, LocalDateTime.now())
				.stream()
				.collect(Collectors.groupingBy(Booking::getItem));

		Map<Item, List<Booking>> nextBookings = bookingRepository
				.findByItemIdAndStartDateAfterOrderByStartDateAsc(itemIds, LocalDateTime.now())
				.stream()
				.collect(Collectors.groupingBy(Booking::getItem));

		return itemMap.values()
				.stream()
				.map(item -> {
					if (isOwner(userId, item)) {
						return makeItemWithBookingsDto(item,
								pastBookings.get(item) == null ? null : pastBookings.get(item).get(0),
								nextBookings.get(item) == null ? null : nextBookings.get(item).get(0));
					} else {
						return makeItemWithBookingsDto(item, null, null);
					}
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemDto> searchBy(long userId, String text, Pageable pageable) {
		userService.getById(userId);
		if (text.isEmpty() || text.isBlank()) {
			return Collections.emptyList();
		}

		PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		return itemRepository.findByNameOrDescriptionContainingAndAvailableTrue(text.toLowerCase(), page)
				.getContent()
				.stream()
				.map(ItemDtoMapper::toItemDto)
				.collect(Collectors.toList());
	}

	@Override
	public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
		Comment comment = CommentDtoMapper.ofCommentRequestDto(commentRequestDto);
		UserDto userDto = userService.getById(commentRequestDto.getAuthorId());
		Item item = itemRepository.findById(commentRequestDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
		comment.setAuthor(UserDtoMapper.ofUserDto(userDto));
		comment.setItem(item);
		comment.setCreated(LocalDateTime.now().plusSeconds(1));
		//Находим завершенное бронирование этой вещи этим пользователем, иначе бросаем ошибку
		Optional<Booking> bookingOpt = bookingRepository.findTop1ByItemIdAndBookerIdAndStatusAndEndDateBefore(
				commentRequestDto.getItemId(),
				commentRequestDto.getAuthorId(),
				Status.APPROVED,
				LocalDateTime.now()
		);
		bookingOpt.orElseThrow(() -> new NotAvailableException("Нужно завершить аренду вещи, чтобы оставить к ней комментарий."));
		return CommentDtoMapper.toCommentResponseDto(commentRepository.save(comment));
	}

	private Item getItem(Long id) {
		Optional<Item> optItem = itemRepository.findById(id);
		return optItem.orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + id));
	}

	private ItemWithFullInfoDto makeItemWithBookingsDto(Item item, Booking lastBooking, Booking nextBooking) {
		ItemWithFullInfoDto itemWithFullInfoDto = ItemDtoMapper.toItemWithFullInfoDto(item);
		if (lastBooking != null) {
			BookingShortDto last = BookingDtoMapper.toBookingShortDto(lastBooking);
			itemWithFullInfoDto.setLastBooking(last);
		}
		if (nextBooking != null) {
			BookingShortDto next = BookingDtoMapper.toBookingShortDto(nextBooking);
			itemWithFullInfoDto.setNextBooking(next);
		}
		return itemWithFullInfoDto;
	}

	private void setRequest(ItemCreateDto itemCreateDto, Item item) {
		if (itemCreateDto.getRequestId() != null) {
			Optional<ItemRequest> optRequest = requestRepository.findById(itemCreateDto.getRequestId());
			item.setRequest(optRequest.orElse(null));
		}
	}

	private void setFieldsToUpdate(ItemUpdateDto itemUpdateDto, Item itemToUpdate) {
		if (itemUpdateDto.getName() != null) {
			itemToUpdate.setName(itemUpdateDto.getName());
		}
		if (itemUpdateDto.getDescription() != null) {
			itemToUpdate.setDescription(itemUpdateDto.getDescription());
		}
		if (itemUpdateDto.getAvailable() != null) {
			itemToUpdate.setAvailable(itemUpdateDto.getAvailable());
		}
	}

	private boolean isOwner(long userId, Item item) {
		return item.getOwner().getId() == userId;
	}
}
