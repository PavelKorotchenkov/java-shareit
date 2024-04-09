package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
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
import ru.practicum.shareit.item.dto.ItemWithFullInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;
	private final ItemService itemService;
	private final UserService userService;

	@Override
	public BookingResponseDto add(BookingRequestDto bookingRequestDto) {
		ItemWithFullInfoDto itemDto = itemService.getById(bookingRequestDto.getItemId(), bookingRequestDto.getBookerId());

		if (bookingRequestDto.getBookerId() == itemDto.getOwnerId()) {
			throw new NotFoundException("Вы не можете забронировать свою вещь.");
		}

		if (!itemDto.getAvailable()) {
			throw new NotAvailableException("Вещь недоступна для бронирования.");
		}

		Booking booking = BookingDtoMapper.toBooking(bookingRequestDto);

		if (booking.getStartDate() == null ||
				booking.getEndDate() == null ||
				booking.getEndDate().isBefore(LocalDateTime.now()) ||
				booking.getStartDate().isBefore(LocalDateTime.now()) ||
				booking.getEndDate().isBefore(booking.getStartDate()) ||
				booking.getStartDate().equals(booking.getEndDate())) {
			throw new BookingDateException("Выбрано некорректное время бронирования.");
		}

		booking.setStatus(Status.WAITING);
		booking.setItem(ItemDtoMapper.toItem(itemDto));
		booking.setBooker(UserDtoMapper.toUser(userService.getById(bookingRequestDto.getBookerId())));

		return BookingDtoMapper.toDto(bookingRepository.save(booking));
	}

	@Override
	public BookingResponseDto approve(long userId, long bookingId, boolean status) {
		UserDto userDto = userService.getById(userId);
		Booking booking = getBooking(bookingId);
		if (booking.getItem().getUser().getId() != userDto.getId()) {
			throw new NotFoundException("У вас нет такого бронирования.");
		}
		if (booking.getStatus().equals(Status.APPROVED)) {
			throw new NotAvailableException("Вы не можете менять статус у подтвержденного бронирования.");
		}
		Item item = ItemDtoMapper.toItem(itemService.getById(booking.getItem().getId(), userId));
		if (!status) {
			booking.setStatus(Status.REJECTED);
		} else {
			booking.setStatus(Status.APPROVED);
		}

		ItemUpdateDto itemUpdateDto = ItemDtoMapper.toItemUpdateDto(item);
		itemUpdateDto.setOwnerId(userId);
		itemService.update(itemUpdateDto);
		return BookingDtoMapper.toDto(bookingRepository.save(booking));
	}

	@Override
	public BookingResponseDto getInfoById(long userId, long bookingId) {
		UserDto userDto = userService.getById(userId);
		BookingResponseDto bookingResponseDto = BookingDtoMapper.toDtoItemOwner(getBooking(bookingId));
		if (bookingResponseDto.getBooker().getId() != userDto.getId() && bookingResponseDto.getItem().getOwnerId() != userDto.getId()) {
			throw new NotFoundException("У вас нет такого бронирования");
		}
		return bookingResponseDto;
	}

	@Override
	public List<BookingResponseDto> getAllBookings(long booker, String state) {
		UserDto userDto = userService.getById(booker);
		List<BookingResponseDto> result;
		LocalDateTime date = LocalDateTime.now();
		if (state.equals(State.ALL.name())) {
			result = getCollect(bookingRepository.findByBookerIdOrderByStartDateDesc(booker));
		} else if (state.equals(State.WAITING.name()) || state.equals(State.REJECTED.name())) {
			result = getCollect(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(booker, Status.valueOf(state)));
		} else if (state.equals(State.PAST.name())) {
			result = getCollect(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(booker, date));
		} else if (state.equals(State.CURRENT.name())) {
			result = getCollect(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(booker, date, date));
		} else if (state.equals(State.FUTURE.name())) {
			result = getCollect(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(booker, date));
		} else {
			throw new UnsupportedOperationException("Unknown state: UNSUPPORTED_STATUS");
		}

		return result;
	}

	@Override
	public List<BookingResponseDto> getAllOwnerBookings(long booker, String state) {
		UserDto userDto = userService.getById(booker);
		List<BookingResponseDto> result;
		LocalDateTime date = LocalDateTime.now();
		if (state.equals(State.ALL.name())) {
			result = getCollect(bookingRepository.findByItemUserIdOrderByStartDateDesc(booker));
		} else if (state.equals(State.WAITING.name()) || state.equals(State.REJECTED.name())) {
			result = getCollect(bookingRepository.findByItemUserIdAndStatusOrderByStartDateDesc(booker, Status.valueOf(state)));
		} else if (state.equals(State.PAST.name())) {
			result = getCollect(bookingRepository.findByItemUserIdAndEndDateBeforeOrderByEndDateDesc(booker, date));
		} else if (state.equals(State.CURRENT.name())) {
			result = getCollect(bookingRepository.findByItemUserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(booker, date, date));
		} else if (state.equals(State.FUTURE.name())) {
			result = getCollect(bookingRepository.findByItemUserIdAndStartDateAfterOrderByStartDateDesc(booker, date));
		} else {
			throw new UnsupportedOperationException("Unknown state: UNSUPPORTED_STATUS");
		}

		return result;
	}

	private List<BookingResponseDto> getCollect(List<Booking> bookingRepository) {
		return bookingRepository
				.stream()
				.map(BookingDtoMapper::toDto)
				.collect(Collectors.toList());
	}

	private Booking getBooking(Long id) {
		Optional<Booking> optBooking = bookingRepository.findById(id);
		return optBooking.orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует: " + id));
	}
}
