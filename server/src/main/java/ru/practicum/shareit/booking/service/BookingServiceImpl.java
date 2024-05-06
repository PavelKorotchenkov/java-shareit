package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final ItemService itemService;
	private final UserService userService;

	private final Sort byStartDateDesc = Sort.by(Sort.Direction.DESC, "StartDate");

	@Override
	public BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto) {
		Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена."));
		if (userId == item.getOwner().getId()) {
			throw new NotFoundException("Вы не можете забронировать свою вещь.");
		}

		if (!item.getAvailable()) {
			throw new NotAvailableException("Вещь недоступна для бронирования.");
		}

		Booking booking = BookingDtoMapper.ofBookingRequestDto(bookingRequestDto);

		if (booking.getStartDate() == null ||
				booking.getEndDate() == null ||
				booking.getEndDate().isBefore(booking.getStartDate()) ||
				booking.getStartDate().equals(booking.getEndDate())) {
			throw new BookingDateException("Выбрано некорректное время бронирования.");
		}

		booking.setStatus(Status.WAITING);
		booking.setItem(item);
		booking.setBooker(UserDtoMapper.ofUserDto(userService.getById(userId)));

		return BookingDtoMapper.toBookingResponseDto(bookingRepository.save(booking));
	}

	@Override
	public BookingResponseDto approve(long userId, long bookingId, boolean status) {
		UserDto userDto = userService.getById(userId);
		Booking booking = getBooking(bookingId);
		if (booking.getItem().getOwner().getId() != userDto.getId()) {
			throw new NotFoundException("У вас нет такого бронирования.");
		}
		if (booking.getStatus().equals(Status.APPROVED)) {
			throw new NotAvailableException("Вы не можете менять статус у подтвержденного бронирования.");
		}
		Item item = ItemDtoMapper.ofItemWithFullInfoDto(itemService.getById(booking.getItem().getId(), userId));
		if (!status) {
			booking.setStatus(Status.REJECTED);
		} else {
			booking.setStatus(Status.APPROVED);
		}

		ItemUpdateDto itemUpdateDto = ItemDtoMapper.toItemUpdateDto(item);
		itemUpdateDto.setOwnerId(userId);
		itemService.update(itemUpdateDto);
		return BookingDtoMapper.toBookingResponseDto(bookingRepository.save(booking));
	}

	@Override
	public BookingResponseDto getInfoById(long userId, long bookingId) {
		UserDto userDto = userService.getById(userId);
		Booking booking = getBooking(bookingId);
		if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
			throw new NotFoundException("У вас нет такого бронирования");
		}
		BookingResponseDto bookingResponseDto = BookingDtoMapper.toBookingResponseDto(getBooking(bookingId));
		return bookingResponseDto;
	}

	@Override
	public List<BookingResponseDto> getAllBookings(long booker, BookingState state, Pageable pageable) {
		userService.getById(booker);
		List<BookingResponseDto> result;
		LocalDateTime date = LocalDateTime.now();
		Pageable sortByStartDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), byStartDateDesc);
		if (state.equals(BookingState.ALL)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByBookerId(booker, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.PAST)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByBookerIdAndEndDateBefore(booker, date, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.CURRENT)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(booker, date, date, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.FUTURE)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByBookerIdAndStartDateAfter(booker, date, sortByStartDateDesc).getContent());
		} else {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByBookerIdAndStatus(booker, Status.valueOf(state.name()), sortByStartDateDesc).getContent());
		}

		return result;
	}

	@Override
	public List<BookingResponseDto> getAllOwnerBookings(long ownerId, BookingState state, Pageable pageable) {
		userService.getById(ownerId);
		List<BookingResponseDto> result;
		LocalDateTime date = LocalDateTime.now();
		Pageable sortByStartDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), byStartDateDesc);
		if (state.equals(BookingState.ALL)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByItemOwnerId(ownerId, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.PAST)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByItemOwnerIdAndEndDateBefore(ownerId, date, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.CURRENT)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(ownerId, date, date, sortByStartDateDesc).getContent());
		} else if (state.equals(BookingState.FUTURE)) {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByItemOwnerIdAndStartDateAfter(ownerId, date, sortByStartDateDesc).getContent());
		} else {
			result = BookingDtoMapper.toBookingResponseDto(bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.valueOf(state.name()), sortByStartDateDesc).getContent());
		}

		return result;
	}

	private Booking getBooking(Long id) {
		Optional<Booking> optBooking = bookingRepository.findById(id);
		return optBooking.orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует: " + id));
	}
}
